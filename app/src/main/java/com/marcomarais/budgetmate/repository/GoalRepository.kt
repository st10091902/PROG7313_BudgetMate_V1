package com.marcomarais.budgetmate.repository

import androidx.lifecycle.LiveData
import com.marcomarais.budgetmate.data.dao.GoalDao
import com.marcomarais.budgetmate.data.entities.Goal

class GoalRepository(private val goalDao: GoalDao) {

    val allGoals: LiveData<List<Goal>> = goalDao.getAllGoals()

    suspend fun insert(goal: Goal) {
        goalDao.insertGoal(goal)
    }

    suspend fun update(goal: Goal) {
        goalDao.updateGoal(goal)
    }

    suspend fun delete(goal: Goal) {
        goalDao.deleteGoal(goal)
    }
}