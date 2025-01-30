package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vsmorodina.myrecipes.data.dao.CategoryDao

class CategoriesViewModelFactory(private val dao: CategoryDao): ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriesViewModel::class.java)) {
            return CategoriesViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}