package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vsmorodina.myrecipes.data.dao.CategoryDao

class ChangeCategoryViewModelFactory(val categoryId: Long, val categoryDao: CategoryDao): ViewModelProvider.Factory{
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChangeCategoryViewModel::class.java)) {
            return ChangeCategoryViewModel(categoryId, categoryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}