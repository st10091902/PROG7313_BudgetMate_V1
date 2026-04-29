package com.marcomarais.budgetmate.repository

import androidx.lifecycle.LiveData
import com.marcomarais.budgetmate.data.dao.TransactionDao
import com.marcomarais.budgetmate.data.entities.Transaction

class TransactionRepository(private val transactionDao: TransactionDao) {

    val allTransactions: LiveData<List<Transaction>> =
        transactionDao.getAllTransactions()

    suspend fun insert(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    fun getTransactionsByDateRange(start: Long, end: Long): LiveData<List<Transaction>> {
        return transactionDao.getTransactionsByDateRange(start, end)
    }
}