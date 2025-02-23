package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vsmorodina.myrecipes.data.dao.CategoryDao

class CreateCategoryViewModelFactory(private val categoryDao: CategoryDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateCategoryViewModel::class.java)) {
            return CreateCategoryViewModel(categoryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}