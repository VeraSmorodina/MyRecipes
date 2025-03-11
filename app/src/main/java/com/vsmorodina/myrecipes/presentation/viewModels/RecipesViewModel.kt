package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.vsmorodina.myrecipes.data.dao.RecipeDao

class RecipesViewModel(categoryId: Long, val dao: RecipeDao) : ViewModel() {
    val recipesLiveData = dao.getAll(categoryId)
}