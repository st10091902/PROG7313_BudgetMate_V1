package com.marcomarais.budgetmate.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.marcomarais.budgetmate.data.dao.CategoryDao
import com.marcomarais.budgetmate.data.dao.GoalDao
import com.marcomarais.budgetmate.data.dao.TransactionDao
import com.marcomarais.budgetmate.data.dao.UserDao
import com.marcomarais.budgetmate.data.entities.Category
import com.marcomarais.budgetmate.data.entities.Goal
import com.marcomarais.budgetmate.data.entities.Transaction
import com.marcomarais.budgetmate.data.entities.User

@Database(
    entities = [
        User::class,
        Category::class,
        Transaction::class,
        Goal::class
    ],
    version = 3,
    exportSchema = false
)
abstract class BudgetMateDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var INSTANCE: BudgetMateDatabase? = null

        fun getDatabase(context: Context): BudgetMateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BudgetMateDatabase::class.java,
                    "budgetmate_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}