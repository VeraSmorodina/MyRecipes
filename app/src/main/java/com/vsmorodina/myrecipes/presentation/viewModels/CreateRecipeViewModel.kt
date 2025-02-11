package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.vsmorodina.myrecipes.data.dao.CategoryDao

class CreateRecipeViewModel(val dao: CategoryDao) : ViewModel() {
    val categories = dao.getAll()
}