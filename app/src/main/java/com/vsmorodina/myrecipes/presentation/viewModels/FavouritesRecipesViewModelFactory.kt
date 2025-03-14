package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vsmorodina.myrecipes.data.dao.RecipeDao

class FavouritesRecipesViewModelFactory(private val dao: RecipeDao): ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavouritesRecipesViewModel::class.java)) {
            return FavouritesRecipesViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}