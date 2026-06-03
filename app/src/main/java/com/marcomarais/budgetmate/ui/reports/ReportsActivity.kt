package com.marcomarais.budgetmate.ui.reports

import android.app.DatePickerDialog
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
import java.util.Calendar
import java.util.Locale
import android.content.Context
import com.marcomarais.budgetmate.firebase.FirebaseService

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
        val barChartView = findViewById<BarChartView>(R.id.barChartView)
        val goalStatusText = findViewById<TextView>(R.id.txtMinMaxGoalStatus)

        val startDateInput = findViewById<EditText>(R.id.edtStartDate)
        val endDateInput = findViewById<EditText>(R.id.edtEndDate)
        val filterButton = findViewById<Button>(R.id.btnFilterReports)
        val backButton = findViewById<Button>(R.id.btnBackHome)
        val btnReadFirebase = findViewById<Button>(R.id.btnReadFirebase)
        val txtFirebaseData = findViewById<TextView>(R.id.txtFirebaseData)

        startDateInput.setOnClickListener {
            showDatePicker(startDateInput)
        }

        endDateInput.setOnClickListener {
            showDatePicker(endDateInput)
        }

        val database = BudgetMateDatabase.getDatabase(this)
        val sharedPreferences = getSharedPreferences("monthly_goals", Context.MODE_PRIVATE)
        val firebaseService = FirebaseService()

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
            displayReport(
                allTransactions,
                txtIncome,
                txtExpenses,
                txtNet,
                txtCategoryTotals,
                barChartView,
                goalStatusText,
                sharedPreferences
            )
        }

        categoryViewModel.allCategories.observe(this) { categories ->
            allCategories = categories
            displayReport(
                allTransactions,
                txtIncome,
                txtExpenses,
                txtNet,
                txtCategoryTotals,
                barChartView,
                goalStatusText,
                sharedPreferences
            )
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

                displayReport(
                    filtered,
                    txtIncome,
                    txtExpenses,
                    txtNet,
                    txtCategoryTotals,
                    barChartView,
                    goalStatusText,
                    sharedPreferences
                )

            } catch (e: Exception) {
                Toast.makeText(this, "Use date format dd/MM/yyyy", Toast.LENGTH_SHORT).show()
            }
        }

        btnReadFirebase.setOnClickListener {
            txtFirebaseData.text = "Loading Firebase data..."

            firebaseService.readTransactionsFromFirebase { firebaseData ->
                txtFirebaseData.text = firebaseData
            }
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun showDatePicker(targetInput: EditText) {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format(
                    "%02d/%02d/%04d",
                    selectedDay,
                    selectedMonth + 1,
                    selectedYear
                )

                targetInput.setText(formattedDate)
            },
            year,
            month,
            day
        )

        datePicker.show()
    }

    private fun displayReport(
        transactions: List<Transaction>,
        txtIncome: TextView,
        txtExpenses: TextView,
        txtNet: TextView,
        txtCategoryTotals: TextView,
        barChartView: BarChartView,
        goalStatusText: TextView,
        sharedPreferences: android.content.SharedPreferences
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

        val minGoal = sharedPreferences.getFloat("minGoal", 0f).toDouble()
        val maxGoal = sharedPreferences.getFloat("maxGoal", 0f).toDouble()

        val goalMessage = when {
            minGoal == 0.0 && maxGoal == 0.0 -> {
                "Monthly min/max goals have not been set yet."
            }
            totalExpenses < minGoal -> {
                "You are below your minimum monthly spending goal."
            }
            totalExpenses > maxGoal -> {
                "You are above your maximum monthly spending goal."
            }
            else -> {
                "You are within your monthly spending goal range."
            }
        }

        goalStatusText.text =
            "Minimum Goal: R%.2f\n".format(minGoal) +
                    "Maximum Goal: R%.2f\n".format(maxGoal) +
                    "Selected Period Spending: R%.2f\n\n".format(totalExpenses) +
                    "Status: $goalMessage"

        val expenses = transactions.filter { it.type == "Expense" }

        if (expenses.isEmpty()) {
            txtCategoryTotals.text = "No category spending found"
            barChartView.setData(emptyList())
            return
        }

        val categoryTotals = expenses.groupBy { it.categoryId }

        val chartData = categoryTotals.entries.map { entry ->
            val categoryName = allCategories.find { it.id == entry.key }?.name ?: "Unknown"
            val total = entry.value.sumOf { it.amount }

            categoryName to total
        }

        txtCategoryTotals.text = chartData.joinToString(separator = "\n\n") {
            "${it.first}: R%.2f".format(it.second)
        }

        barChartView.setData(chartData)
    }
}