package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsmorodina.myrecipes.data.dao.CategoryDao
import kotlinx.coroutines.launch

class CategoriesViewModel(val dao: CategoryDao) : ViewModel() {
    val categoriesLiveData = dao.getAllLiveData()

    fun deleteCategory(categoryId: Long){
        viewModelScope.launch {
            dao.deleteById(categoryId = categoryId)
        }
    }
}