package com.marcomarais.budgetmate.ui.budgets

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.marcomarais.budgetmate.R
import com.marcomarais.budgetmate.data.BudgetMateDatabase
import com.marcomarais.budgetmate.data.entities.Category
import com.marcomarais.budgetmate.repository.CategoryRepository
import com.marcomarais.budgetmate.viewmodel.CategoryViewModel
import com.marcomarais.budgetmate.viewmodel.CategoryViewModelFactory
import com.marcomarais.budgetmate.firebase.FirebaseService

class BudgetsActivity : AppCompatActivity() {

    private lateinit var categoryViewModel: CategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budgets)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Budgets"

        val nameInput = findViewById<EditText>(R.id.edtBudgetName)
        val amountInput = findViewById<EditText>(R.id.edtBudgetAmount)
        val addButton = findViewById<Button>(R.id.btnAddBudget)
        val budgetsList = findViewById<TextView>(R.id.txtBudgetsList)
        val backButton = findViewById<Button>(R.id.btnBackHome)

        val database = BudgetMateDatabase.getDatabase(this)
        val repository = CategoryRepository(database.categoryDao())
        val factory = CategoryViewModelFactory(repository)

        categoryViewModel =
            ViewModelProvider(this, factory)[CategoryViewModel::class.java]

        categoryViewModel.allCategories.observe(this) { categories ->
            if (categories.isEmpty()) {
                budgetsList.text = "No budgets added yet"
            } else {
                budgetsList.text = categories.joinToString(separator = "\n\n") {
                    "${it.name}\nBudget: R%.2f".format(it.budgetAmount)
                }
            }
        }
        val firebaseService = FirebaseService()

        addButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val amountText = amountInput.text.toString().trim()
            val amount = amountText.toDoubleOrNull()

            if (name.isEmpty() || amount == null || amount <= 0) {
                Toast.makeText(this, "Please enter a valid budget name and amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val category = Category(
                name = name,
                budgetAmount = amount
            )

            categoryViewModel.insert(category)
            firebaseService.uploadCategory(category)

            nameInput.text.clear()
            amountInput.text.clear()

            Toast.makeText(this, "Budget envelope added", Toast.LENGTH_SHORT).show()
        }
        backButton.setOnClickListener {
            finish()
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}