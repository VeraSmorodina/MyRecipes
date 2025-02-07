package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.vsmorodina.myrecipes.data.dao.RecipeDao

class RecipeViewModel (private val recipeId: Long, private val dao: RecipeDao) : ViewModel() {
    val recipe = dao.get(recipeId)
}