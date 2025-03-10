package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vsmorodina.myrecipes.data.dao.CategoryDao
import com.vsmorodina.myrecipes.data.dao.RecipeDao

class CreateRecipeViewModelFactory(private val categoryDao: CategoryDao, private val recipeDao: RecipeDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateRecipeViewModel::class.java)) {
            return CreateRecipeViewModel(categoryDao,recipeDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}