package com.marcomarais.budgetmate.ui.reports

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportsActivity : AppCompatActivity() {

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var categoryViewModel: CategoryViewModel

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private var allTransactions = listOf<Transaction>()
    private var allCategories = listOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        val txtIncome = findViewById<TextView>(R.id.txtTotalIncome)
        val txtExpenses = findViewById<TextView>(R.id.txtTotalExpenses)
        val txtNet = findViewById<TextView>(R.id.txtNetBalance)
        val txtCategoryTotals = findViewById<TextView>(R.id.txtCategoryTotals)

        val startDateInput = findViewById<EditText>(R.id.edtStartDate)
        val endDateInput = findViewById<EditText>(R.id.edtEndDate)
        val filterButton = findViewById<Button>(R.id.btnFilterReports)
        val backButton = findViewById<Button>(R.id.btnBackHome)

        val database = BudgetMateDatabase.getDatabase(this)

        val transactionRepository = TransactionRepository(database.transactionDao())
        val transactionFactory = TransactionViewModelFactory(transactionRepository)
        transactionViewModel =
            ViewModelProvider(this, transactionFactory)[TransactionViewModel::class.java]

        val categoryRepository = CategoryRepository(database.categoryDao())
        val categoryFactory = CategoryViewModelFactory(categoryRepository)
        categoryViewModel =
            ViewModelProvider(this, categoryFactory)[CategoryViewModel::class.java]

        transactionViewModel.allTransactions.observe(this) { transactions ->
            allTransactions = transactions
            displayReport(allTransactions, txtIncome, txtExpenses, txtNet, txtCategoryTotals)
        }

        categoryViewModel.allCategories.observe(this) { categories ->
            allCategories = categories
            displayReport(allTransactions, txtIncome, txtExpenses, txtNet, txtCategoryTotals)
        }

        filterButton.setOnClickListener {
            val startText = startDateInput.text.toString().trim()
            val endText = endDateInput.text.toString().trim()

            if (startText.isEmpty() || endText.isEmpty()) {
                Toast.makeText(this, "Please enter both dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val startDate = dateFormat.parse(startText)?.time ?: 0L
                val endDate = (dateFormat.parse(endText)?.time ?: 0L) + 86_400_000

                val filtered = allTransactions.filter {
                    it.date in startDate..endDate
                }

                displayReport(filtered, txtIncome, txtExpenses, txtNet, txtCategoryTotals)

            } catch (e: Exception) {
                Toast.makeText(this, "Use date format dd/MM/yyyy", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun displayReport(
        transactions: List<Transaction>,
        txtIncome: TextView,
        txtExpenses: TextView,
        txtNet: TextView,
        txtCategoryTotals: TextView
    ) {
        var totalIncome = 0.0
        var totalExpenses = 0.0

        for (transaction in transactions) {
            if (transaction.type == "Income") {
                totalIncome += transaction.amount
            } else {
                totalExpenses += transaction.amount
            }
        }

        txtIncome.text = "Total Income: R%.2f".format(totalIncome)
        txtExpenses.text = "Total Expenses: R%.2f".format(totalExpenses)
        txtNet.text = "Net Balance: R%.2f".format(totalIncome - totalExpenses)

        val expenses = transactions.filter { it.type == "Expense" }

        if (expenses.isEmpty()) {
            txtCategoryTotals.text = "No category spending found"
            return
        }

        val categoryTotals = expenses.groupBy { it.categoryId }

        txtCategoryTotals.text = categoryTotals.entries.joinToString(separator = "\n\n") { entry ->
            val categoryName = allCategories.find { it.id == entry.key }?.name ?: "Unknown Category"
            val total = entry.value.sumOf { it.amount }

            "$categoryName: R%.2f".format(total)
        }
    }
}