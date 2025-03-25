package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vsmorodina.myrecipes.data.dao.RecipeDao

class SearchRecipeViewModelFactory(private val substring: String, private val dao: RecipeDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchRecipeViewModel::class.java)) {
            return SearchRecipeViewModel(substring, dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}