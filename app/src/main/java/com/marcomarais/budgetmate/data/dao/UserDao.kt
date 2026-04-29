package com.marcomarais.budgetmate.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.marcomarais.budgetmate.data.entities.User

@Dao
interface UserDao {

    @Insert
    suspend fun registerUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun loginUser(email: String, password: String): User?
}