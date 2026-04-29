package com.marcomarais.budgetmate.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.marcomarais.budgetmate.data.entities.Category

@Dao
interface CategoryDao {

    @Insert
    suspend fun insertCategory(category: Category)

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): LiveData<List<Category>>
}