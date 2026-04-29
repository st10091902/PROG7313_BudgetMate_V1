package com.marcomarais.budgetmate.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.marcomarais.budgetmate.data.entities.Goal

@Dao
interface GoalDao {

    @Insert
    suspend fun insertGoal(goal: Goal)

    @Update
    suspend fun updateGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)

    @Query("SELECT * FROM goals ORDER BY name ASC")
    fun getAllGoals(): LiveData<List<Goal>>
}