package com.marcomarais.budgetmate.repository

import androidx.lifecycle.LiveData
import com.marcomarais.budgetmate.data.dao.CategoryDao
import com.marcomarais.budgetmate.data.entities.Category

class CategoryRepository(private val categoryDao: CategoryDao) {

    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()

    suspend fun insert(category: Category) {
        categoryDao.insertCategory(category)
    }

    suspend fun update(category: Category) {
        categoryDao.updateCategory(category)
    }

    suspend fun delete(category: Category) {
        categoryDao.deleteCategory(category)
    }
}