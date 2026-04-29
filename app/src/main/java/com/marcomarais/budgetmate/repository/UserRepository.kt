package com.marcomarais.budgetmate.repository

import com.marcomarais.budgetmate.data.dao.UserDao
import com.marcomarais.budgetmate.data.entities.User

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(user: User) {
        userDao.registerUser(user)
    }

    suspend fun loginUser(email: String, password: String): User? {
        return userDao.loginUser(email, password)
    }
}