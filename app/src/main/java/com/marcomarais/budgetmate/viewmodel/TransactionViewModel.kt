package com.marcomarais.budgetmate.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcomarais.budgetmate.data.entities.Transaction
import com.marcomarais.budgetmate.repository.TransactionRepository
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    val allTransactions: LiveData<List<Transaction>> = repository.allTransactions

    fun insert(transaction: Transaction) {
        viewModelScope.launch {
            repository.insert(transaction)
        }
    }
}