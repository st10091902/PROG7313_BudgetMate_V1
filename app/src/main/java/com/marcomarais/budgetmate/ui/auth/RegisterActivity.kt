package com.marcomarais.budgetmate.ui.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.marcomarais.budgetmate.R
import com.marcomarais.budgetmate.data.BudgetMateDatabase
import com.marcomarais.budgetmate.repository.UserRepository
import com.marcomarais.budgetmate.viewmodel.AuthViewModel
import com.marcomarais.budgetmate.viewmodel.AuthViewModelFactory
import android.util.Log

class RegisterActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel
    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val database = BudgetMateDatabase.getDatabase(this)
        val repository = UserRepository(database.userDao())
        val factory = AuthViewModelFactory(repository)

        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        val emailInput = findViewById<EditText>(R.id.edtRegisterEmail)
        val passwordInput = findViewById<EditText>(R.id.edtRegisterPassword)
        val confirmPasswordInput = findViewById<EditText>(R.id.edtConfirmPassword)
        val registerButton = findViewById<Button>(R.id.btnRegister)
        val goToLogin = findViewById<TextView>(R.id.txtGoToLogin)

        registerButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.register(email, password) {
                Log.d(TAG, "User registered: $email")

                Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        goToLogin.setOnClickListener {
            finish()
        }
    }
}