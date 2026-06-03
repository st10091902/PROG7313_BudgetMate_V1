package com.marcomarais.budgetmate.ui.expenses

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.marcomarais.budgetmate.R
import com.marcomarais.budgetmate.data.BudgetMateDatabase
import com.marcomarais.budgetmate.data.entities.Transaction
import com.marcomarais.budgetmate.repository.TransactionRepository
import com.marcomarais.budgetmate.viewmodel.TransactionViewModel
import com.marcomarais.budgetmate.viewmodel.TransactionViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.app.DatePickerDialog
import java.util.Calendar

class ExpenseListActivity : AppCompatActivity() {

    private lateinit var transactionViewModel: TransactionViewModel
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var allExpenses = listOf<Transaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_list)

        val expenseListText = findViewById<TextView>(R.id.txtExpenseList)
        val backButton = findViewById<Button>(R.id.btnBackHome)
        val startDateInput = findViewById<EditText>(R.id.edtStartDate)
        startDateInput.setOnClickListener {
            showDatePicker(startDateInput)
        }
        val endDateInput = findViewById<EditText>(R.id.edtEndDate)
        endDateInput.setOnClickListener {
            showDatePicker(endDateInput)
        }
        val filterButton = findViewById<Button>(R.id.btnFilter)

        val database = BudgetMateDatabase.getDatabase(this)
        val repository = TransactionRepository(database.transactionDao())
        val factory = TransactionViewModelFactory(repository)

        transactionViewModel =
            ViewModelProvider(this, factory)[TransactionViewModel::class.java]

        transactionViewModel.allTransactions.observe(this) { transactions ->
            allExpenses = transactions.filter { it.type == "Expense" }
            displayExpenses(allExpenses, expenseListText)
        }

        filterButton.setOnClickListener {
            val startDateText = startDateInput.text.toString().trim()
            val endDateText = endDateInput.text.toString().trim()

            if (startDateText.isEmpty() || endDateText.isEmpty()) {
                Toast.makeText(this, "Please enter both dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val startDate = dateFormat.parse(startDateText)?.time ?: 0L
                val endDate = (dateFormat.parse(endDateText)?.time ?: 0L) + 86_400_000

                val filteredExpenses = allExpenses.filter {
                    it.date in startDate..endDate
                }

                displayExpenses(filteredExpenses, expenseListText)

            } catch (e: Exception) {
                Toast.makeText(this, "Use date format dd/MM/yyyy", Toast.LENGTH_SHORT).show()
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

    private fun displayExpenses(expenses: List<Transaction>, expenseListText: TextView) {
        if (expenses.isEmpty()) {
            expenseListText.text = "No expenses found"
        } else {
            expenseListText.text = expenses.joinToString(separator = "\n\n") {
                val formattedDate = dateFormat.format(Date(it.date))

                "Date: $formattedDate\n" +
                        "Time: ${it.startTime} - ${it.endTime}\n" +
                        "Description: ${it.description}\n" +
                        "Amount: R%.2f".format(it.amount)
            }
        }
    }
}