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
import android.net.Uri

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private val TAG = "AddTransactionActivity"
    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

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

        val amountInput = findViewById<EditText>(R.id.edtAmount)
        val descriptionInput = findViewById<EditText>(R.id.edtDescription)
        val typeSpinner = findViewById<Spinner>(R.id.spinnerType)
        val categorySpinner = findViewById<Spinner>(R.id.spinnerCategory)
        val saveButton = findViewById<Button>(R.id.btnSaveTransaction)
        val backButton = findViewById<Button>(R.id.btnBackHome)
        val startTimeInput = findViewById<EditText>(R.id.edtStartTime)
        val endTimeInput = findViewById<EditText>(R.id.edtEndTime)
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
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
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

            Log.d(TAG, "Transaction saved: $transaction")

            Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show()
            finish()
        }

        backButton.setOnClickListener {
            finish()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            val imgPhoto = findViewById<ImageView>(R.id.imgExpensePhoto)

            imgPhoto.setImageURI(selectedImageUri)
            imgPhoto.visibility = ImageView.VISIBLE
        }
    }
}