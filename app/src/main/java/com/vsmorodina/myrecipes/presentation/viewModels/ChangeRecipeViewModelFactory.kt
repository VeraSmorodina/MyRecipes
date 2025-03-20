package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vsmorodina.myrecipes.data.dao.CategoryDao
import com.vsmorodina.myrecipes.data.dao.RecipeDao

class ChangeRecipeViewModelFactory(private val recipeId: Long, private val dao: RecipeDao, private val categoryDao: CategoryDao): ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChangeRecipeViewModel::class.java)) {
            return ChangeRecipeViewModel(recipeId, dao, categoryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}