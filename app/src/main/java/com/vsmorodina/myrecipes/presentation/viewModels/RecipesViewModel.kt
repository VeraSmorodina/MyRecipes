package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.vsmorodina.myrecipes.data.dao.RecipeDao

class RecipesViewModel (val dao: RecipeDao) : ViewModel() {
    val recipesLiveData = dao.getAll()
}