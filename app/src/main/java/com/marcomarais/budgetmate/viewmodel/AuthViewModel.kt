package com.marcomarais.budgetmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcomarais.budgetmate.data.entities.User
import com.marcomarais.budgetmate.repository.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    fun register(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.registerUser(User(email = email, password = password))
            onResult(true)
        }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = repository.loginUser(email, password)
            onResult(user != null)
        }
    }
}