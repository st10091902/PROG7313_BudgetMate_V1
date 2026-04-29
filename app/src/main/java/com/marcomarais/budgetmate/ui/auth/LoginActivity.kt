package com.marcomarais.budgetmate.ui.auth

import android.content.Intent
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

class LoginActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val database = BudgetMateDatabase.getDatabase(this)
        val repository = UserRepository(database.userDao())
        val factory = AuthViewModelFactory(repository)

        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        val emailInput = findViewById<EditText>(R.id.edtLoginEmail)
        val passwordInput = findViewById<EditText>(R.id.edtLoginPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val goToRegister = findViewById<TextView>(R.id.txtGoToRegister)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.login(email, password) { success ->
                if (success) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "User logged in successfully: $email")

                    val intent = Intent(this, com.marcomarais.budgetmate.ui.home.HomeActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Log.d(TAG, "Failed login attempt for: $email")
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        goToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}