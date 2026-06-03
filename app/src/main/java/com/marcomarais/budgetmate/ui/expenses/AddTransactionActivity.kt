package com.marcomarais.budgetmate.ui.expenses

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.marcomarais.budgetmate.R
import com.marcomarais.budgetmate.data.BudgetMateDatabase
import com.marcomarais.budgetmate.data.entities.Category
import com.marcomarais.budgetmate.data.entities.Transaction
import com.marcomarais.budgetmate.repository.CategoryRepository
import com.marcomarais.budgetmate.repository.TransactionRepository
import com.marcomarais.budgetmate.viewmodel.CategoryViewModel
import com.marcomarais.budgetmate.viewmodel.CategoryViewModelFactory
import com.marcomarais.budgetmate.viewmodel.TransactionViewModel
import com.marcomarais.budgetmate.viewmodel.TransactionViewModelFactory
import android.app.Activity
import android.content.Intent
import android.app.TimePickerDialog
import java.util.Calendar
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import com.marcomarais.budgetmate.firebase.FirebaseService

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private val TAG = "AddTransactionActivity"
    private var selectedImageUri: Uri? = null
    private val CAMERA_REQUEST_CODE = 100
    private val CAMERA_PERMISSION_CODE = 101

    private var categoryList = listOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        val database = BudgetMateDatabase.getDatabase(this)

        val transactionRepository = TransactionRepository(database.transactionDao())
        val transactionFactory = TransactionViewModelFactory(transactionRepository)
        transactionViewModel =
            ViewModelProvider(this, transactionFactory)[TransactionViewModel::class.java]

        val categoryRepository = CategoryRepository(database.categoryDao())
        val categoryFactory = CategoryViewModelFactory(categoryRepository)
        categoryViewModel =
            ViewModelProvider(this, categoryFactory)[CategoryViewModel::class.java]

        val firebaseService = FirebaseService()
        val amountInput = findViewById<EditText>(R.id.edtAmount)
        val descriptionInput = findViewById<EditText>(R.id.edtDescription)
        val typeSpinner = findViewById<Spinner>(R.id.spinnerType)
        val categorySpinner = findViewById<Spinner>(R.id.spinnerCategory)
        val saveButton = findViewById<Button>(R.id.btnSaveTransaction)
        val backButton = findViewById<Button>(R.id.btnBackHome)
        val startTimeInput = findViewById<EditText>(R.id.edtStartTime)
        startTimeInput.setOnClickListener {
            showTimePicker(startTimeInput)
        }
        val endTimeInput = findViewById<EditText>(R.id.edtEndTime)
        endTimeInput.setOnClickListener {
            showTimePicker(endTimeInput)
        }
        val btnChoosePhoto = findViewById<Button>(R.id.btnChoosePhoto)
        val imgPhoto = findViewById<ImageView>(R.id.imgExpensePhoto)

        val types = listOf("Income", "Expense")
        typeSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            types
        )

        categoryViewModel.allCategories.observe(this) { categories ->
            categoryList = categories

            val categoryNames = categories.map { it.name }

            categorySpinner.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categoryNames
            )
        }

        btnChoosePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            } else {
                openCamera()
            }
        }

        saveButton.setOnClickListener {
            val amountText = amountInput.text.toString().trim()
            val description = descriptionInput.text.toString().trim()
            val type = typeSpinner.selectedItem.toString()
            val startTime = startTimeInput.text.toString().trim()
            val endTime = endTimeInput.text.toString().trim()

            if (amountText.isEmpty() || description.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (type == "Expense" && categoryList.isEmpty()) {
                Toast.makeText(this, "Please create a budget category first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()

            if (amount == null || amount <= 0) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedCategoryId = if (type == "Expense") {
                categoryList[categorySpinner.selectedItemPosition].id
            } else {
                0
            }

            val transaction = Transaction(
                categoryId = selectedCategoryId,
                amount = amount,
                type = type,
                description = description,
                date = System.currentTimeMillis(),
                startTime = startTime,
                endTime = endTime,
                photoUri = selectedImageUri?.toString()
            )

            transactionViewModel.insert(transaction)
            firebaseService.uploadTransaction(transaction)

            Log.d(TAG, "Transaction saved: $transaction")

            Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show()
            finish()
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    private fun showTimePicker(targetInput: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                targetInput.setText(formattedTime)
            },
            hour,
            minute,
            true
        )

        timePicker.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as? Bitmap

            if (imageBitmap != null) {
                val imageUri = saveBitmapToInternalStorage(imageBitmap)

                selectedImageUri = imageUri

                val imgPhoto = findViewById<ImageView>(R.id.imgExpensePhoto)
                imgPhoto.setImageBitmap(imageBitmap)
                imgPhoto.visibility = ImageView.VISIBLE

                Log.d(TAG, "Camera photo captured and saved: $selectedImageUri")
            } else {
                Toast.makeText(this, "Could not capture photo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveBitmapToInternalStorage(bitmap: Bitmap): Uri {
        val fileName = "expense_photo_${System.currentTimeMillis()}.jpg"
        val file = File(filesDir, fileName)

        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        return Uri.fromFile(file)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission is required to take a photo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}