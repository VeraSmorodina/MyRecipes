package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsmorodina.myrecipes.data.dao.RecipeDao
import kotlinx.coroutines.launch

class SearchRecipeViewModel(query: String, val dao: RecipeDao) : ViewModel() {
    val recipesLiveData = dao.searchRecipes(query)
}