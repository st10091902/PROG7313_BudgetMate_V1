package com.marcomarais.budgetmate.ui.goals

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.marcomarais.budgetmate.R
import com.marcomarais.budgetmate.data.BudgetMateDatabase
import com.marcomarais.budgetmate.data.entities.Goal
import com.marcomarais.budgetmate.repository.GoalRepository
import com.marcomarais.budgetmate.viewmodel.GoalViewModel
import com.marcomarais.budgetmate.viewmodel.GoalViewModelFactory

class GoalsActivity : AppCompatActivity() {

    private lateinit var goalViewModel: GoalViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Savings Goals"

        val nameInput = findViewById<EditText>(R.id.edtGoalName)
        val targetInput = findViewById<EditText>(R.id.edtTargetAmount)
        val currentInput = findViewById<EditText>(R.id.edtCurrentAmount)
        val addButton = findViewById<Button>(R.id.btnAddGoal)
        val goalsList = findViewById<TextView>(R.id.txtGoalsList)

        val database = BudgetMateDatabase.getDatabase(this)
        val repository = GoalRepository(database.goalDao())
        val factory = GoalViewModelFactory(repository)

        goalViewModel = ViewModelProvider(this, factory)[GoalViewModel::class.java]

        goalViewModel.allGoals.observe(this) { goals ->
            goalsList.text =
                if (goals.isEmpty()) {
                    "No goals added yet"
                } else {
                    goals.joinToString(separator = "\n\n") {
                        val progress = if (it.targetAmount > 0) {
                            (it.currentAmount / it.targetAmount) * 100
                        } else {
                            0.0
                        }

                        "${it.name}\nSaved: R%.2f / R%.2f\nProgress: %.1f%%"
                            .format(it.currentAmount, it.targetAmount, progress)
                    }
                }
        }

        addButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val target = targetInput.text.toString().trim().toDoubleOrNull()
            val current = currentInput.text.toString().trim().toDoubleOrNull()

            if (name.isEmpty() || target == null || current == null || target <= 0 || current < 0) {
                Toast.makeText(this, "Please enter valid goal details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            goalViewModel.insert(
                Goal(
                    name = name,
                    targetAmount = target,
                    currentAmount = current
                )
            )

            nameInput.text.clear()
            targetInput.text.clear()
            currentInput.text.clear()

            Toast.makeText(this, "Savings goal added", Toast.LENGTH_SHORT).show()
        }
        val backButton = findViewById<Button>(R.id.btnBackHome)

        backButton.setOnClickListener {
            finish()
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}