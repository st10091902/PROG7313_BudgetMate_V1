package com.marcomarais.budgetmate.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val categoryId: Int,
    val amount: Double,
    val type: String,
    val description: String,
    val date: Long,
    val startTime: String,
    val endTime: String,
    val photoUri: String? = null
)