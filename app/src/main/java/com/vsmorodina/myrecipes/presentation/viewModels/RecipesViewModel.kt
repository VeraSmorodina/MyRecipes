package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vsmorodina.myrecipes.data.dao.RecipeDao

class RecipesViewModel(private val categoryId: Long, val dao: RecipeDao) : ViewModel() {
//    val recipesLiveData = dao.getAll()
    val recipesLiveData = dao.getAll(categoryId)

    private val _navigateToRecipe = MutableLiveData<Long?>()
    val navigateToRecipe: LiveData<Long?>
        get() = _navigateToRecipe

    fun onRecipeClicked(recipeId: Long) {
        _navigateToRecipe.value = recipeId
    }

    fun onRecipeNavigated() {
        _navigateToRecipe.value = null
    }
}