package com.marcomarais.budgetmate.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.marcomarais.budgetmate.data.entities.Category
import com.marcomarais.budgetmate.data.entities.Goal
import com.marcomarais.budgetmate.data.entities.Transaction

class FirebaseService {

    private val db = FirebaseFirestore.getInstance()

    fun uploadTransaction(transaction: Transaction) {
        val transactionData = hashMapOf(
            "localId" to transaction.id,
            "categoryId" to transaction.categoryId,
            "amount" to transaction.amount,
            "type" to transaction.type,
            "description" to transaction.description,
            "date" to transaction.date,
            "startTime" to transaction.startTime,
            "endTime" to transaction.endTime,
            "photoUri" to transaction.photoUri
        )

        db.collection("transactions")
            .add(transactionData)
            .addOnSuccessListener {
                Log.d("FirebaseService", "Transaction uploaded successfully")
            }
            .addOnFailureListener { error ->
                Log.e("FirebaseService", "Transaction upload failed", error)
            }
    }

    fun uploadCategory(category: Category) {
        val categoryData = hashMapOf(
            "localId" to category.id,
            "name" to category.name,
            "budgetAmount" to category.budgetAmount
        )

        db.collection("categories")
            .add(categoryData)
            .addOnSuccessListener {
                Log.d("FirebaseService", "Category uploaded successfully")
            }
            .addOnFailureListener { error ->
                Log.e("FirebaseService", "Category upload failed", error)
            }
    }

    fun uploadGoal(goal: Goal) {
        val goalData = hashMapOf(
            "localId" to goal.id,
            "name" to goal.name,
            "targetAmount" to goal.targetAmount,
            "currentAmount" to goal.currentAmount
        )

        db.collection("goals")
            .add(goalData)
            .addOnSuccessListener {
                Log.d("FirebaseService", "Goal uploaded successfully")
            }
            .addOnFailureListener { error ->
                Log.e("FirebaseService", "Goal upload failed", error)
            }
    }

    fun readTransactionsFromFirebase(onResult: (String) -> Unit) {
        db.collection("transactions")
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    onResult("No transactions found in Firebase.")
                    return@addOnSuccessListener
                }

                val firebaseData = StringBuilder()

                for (document in result) {
                    val amount = document.getDouble("amount") ?: 0.0
                    val type = document.getString("type") ?: "Unknown"
                    val description = document.getString("description") ?: "No description"
                    val startTime = document.getString("startTime") ?: ""
                    val endTime = document.getString("endTime") ?: ""

                    firebaseData.append("Type: $type\n")
                    firebaseData.append("Amount: R%.2f\n".format(amount))
                    firebaseData.append("Description: $description\n")
                    firebaseData.append("Time: $startTime - $endTime\n")
                    firebaseData.append("----------------------\n")
                }

                onResult(firebaseData.toString())
            }
            .addOnFailureListener { error ->
                onResult("Failed to read Firebase data: ${error.message}")
            }
    }
}