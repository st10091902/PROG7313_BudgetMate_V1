package com.marcomarais.budgetmate.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcomarais.budgetmate.data.entities.Category
import com.marcomarais.budgetmate.repository.CategoryRepository
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val repository: CategoryRepository
) : ViewModel() {

    val allCategories: LiveData<List<Category>> = repository.allCategories

    fun insert(category: Category) {
        viewModelScope.launch {
            repository.insert(category)
        }
    }
}