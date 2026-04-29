package com.marcomarais.budgetmate.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.marcomarais.budgetmate.R
import com.marcomarais.budgetmate.data.BudgetMateDatabase
import com.marcomarais.budgetmate.repository.TransactionRepository
import com.marcomarais.budgetmate.ui.budgets.BudgetsActivity
import com.marcomarais.budgetmate.ui.expenses.AddTransactionActivity
import com.marcomarais.budgetmate.viewmodel.TransactionViewModel
import com.marcomarais.budgetmate.viewmodel.TransactionViewModelFactory
import com.marcomarais.budgetmate.ui.goals.GoalsActivity
import com.marcomarais.budgetmate.ui.reports.ReportsActivity
import com.marcomarais.budgetmate.ui.expenses.ExpenseListActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var transactionViewModel: TransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val txtBalance = findViewById<TextView>(R.id.txtBalance)
        val btnAddTransaction = findViewById<Button>(R.id.btnAddTransaction)
        val btnBudgets = findViewById<Button>(R.id.btnBudgets)
        val btnGoals = findViewById<Button>(R.id.btnGoals)
        val btnReports = findViewById<Button>(R.id.btnReports)
        val btnExpenseList = findViewById<Button>(R.id.btnExpenseList)

        val database = BudgetMateDatabase.getDatabase(this)
        val repository = TransactionRepository(database.transactionDao())
        val factory = TransactionViewModelFactory(repository)

        transactionViewModel =
            ViewModelProvider(this, factory)[TransactionViewModel::class.java]

        transactionViewModel.allTransactions.observe(this) { transactions ->
            var balance = 0.0

            for (transaction in transactions) {
                if (transaction.type == "Income") {
                    balance += transaction.amount
                } else {
                    balance -= transaction.amount
                }
            }

            txtBalance.text = "Balance: R%.2f".format(balance)
        }

        btnAddTransaction.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        btnBudgets.setOnClickListener {
            startActivity(Intent(this, BudgetsActivity::class.java))
        }

        btnGoals.setOnClickListener {
            startActivity(Intent(this, GoalsActivity::class.java))
        }

        btnReports.setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java))
        }

        btnExpenseList.setOnClickListener {
            startActivity(Intent(this, ExpenseListActivity::class.java))
        }
    }
}