package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.vsmorodina.myrecipes.data.dao.CategoryDao

class CategoriesViewModel(val dao: CategoryDao) : ViewModel() {
    val categoriesLiveData = dao.getAllLiveData()
}