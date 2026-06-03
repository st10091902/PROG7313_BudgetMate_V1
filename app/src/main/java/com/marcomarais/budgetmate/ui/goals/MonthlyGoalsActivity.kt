package com.marcomarais.budgetmate.ui.goals

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.marcomarais.budgetmate.R
import com.marcomarais.budgetmate.data.BudgetMateDatabase
import com.marcomarais.budgetmate.repository.TransactionRepository
import com.marcomarais.budgetmate.viewmodel.TransactionViewModel
import com.marcomarais.budgetmate.viewmodel.TransactionViewModelFactory
import java.util.Calendar

class MonthlyGoalsActivity : AppCompatActivity() {

    private lateinit var transactionViewModel: TransactionViewModel

    private var currentMonthExpenseTotal = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_goals)

        val minGoalInput = findViewById<EditText>(R.id.edtMinGoal)
        val maxGoalInput = findViewById<EditText>(R.id.edtMaxGoal)
        val saveButton = findViewById<Button>(R.id.btnSaveMonthlyGoals)
        val statusText = findViewById<TextView>(R.id.txtGoalStatus)
        val backButton = findViewById<Button>(R.id.btnBackHome)

        val sharedPreferences = getSharedPreferences("monthly_goals", Context.MODE_PRIVATE)

        val savedMinGoal = sharedPreferences.getFloat("minGoal", 0f)
        val savedMaxGoal = sharedPreferences.getFloat("maxGoal", 0f)

        if (savedMinGoal > 0) {
            minGoalInput.setText(savedMinGoal.toString())
        }

        if (savedMaxGoal > 0) {
            maxGoalInput.setText(savedMaxGoal.toString())
        }

        val database = BudgetMateDatabase.getDatabase(this)
        val repository = TransactionRepository(database.transactionDao())
        val factory = TransactionViewModelFactory(repository)

        transactionViewModel =
            ViewModelProvider(this, factory)[TransactionViewModel::class.java]

        transactionViewModel.allTransactions.observe(this) { transactions ->
            currentMonthExpenseTotal = transactions
                .filter { it.type == "Expense" && isInCurrentMonth(it.date) }
                .sumOf { it.amount }

            val minGoal = sharedPreferences.getFloat("minGoal", 0f).toDouble()
            val maxGoal = sharedPreferences.getFloat("maxGoal", 0f).toDouble()

            updateGoalStatus(statusText, minGoal, maxGoal)
        }

        saveButton.setOnClickListener {
            val minGoal = minGoalInput.text.toString().trim().toDoubleOrNull()
            val maxGoal = maxGoalInput.text.toString().trim().toDoubleOrNull()

            if (minGoal == null || maxGoal == null || minGoal < 0 || maxGoal <= 0) {
                Toast.makeText(this, "Please enter valid minimum and maximum goals", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (minGoal > maxGoal) {
                Toast.makeText(this, "Minimum goal cannot be greater than maximum goal", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sharedPreferences.edit()
                .putFloat("minGoal", minGoal.toFloat())
                .putFloat("maxGoal", maxGoal.toFloat())
                .apply()

            updateGoalStatus(statusText, minGoal, maxGoal)

            Toast.makeText(this, "Monthly goals saved", Toast.LENGTH_SHORT).show()
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun isInCurrentMonth(dateMillis: Long): Boolean {
        val transactionCalendar = Calendar.getInstance()
        transactionCalendar.timeInMillis = dateMillis

        val currentCalendar = Calendar.getInstance()

        return transactionCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) &&
                transactionCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)
    }

    private fun updateGoalStatus(statusText: TextView, minGoal: Double, maxGoal: Double) {
        if (minGoal == 0.0 && maxGoal == 0.0) {
            statusText.text = "Set your monthly minimum and maximum goals."
            return
        }

        val statusMessage = when {
            currentMonthExpenseTotal < minGoal -> {
                "You are below your minimum monthly spending goal."
            }

            currentMonthExpenseTotal > maxGoal -> {
                "You are above your maximum monthly spending goal."
            }

            else -> {
                "You are within your monthly spending goal range."
            }
        }

        statusText.text =
            "Minimum Goal: R%.2f\n".format(minGoal) +
                    "Maximum Goal: R%.2f\n".format(maxGoal) +
                    "Current Month Spending: R%.2f\n\n".format(currentMonthExpenseTotal) +
                    "Status: $statusMessage"
    }
}