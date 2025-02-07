package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vsmorodina.myrecipes.data.dao.RecipeDao

class RecipesViewModel(val dao: RecipeDao) : ViewModel() {
    val recipesLiveData = dao.getAll()

    private val _navigateToRecipe = MutableLiveData<Long?>()
    val navigateToRecipe: LiveData<Long?>
        get() = _navigateToRecipe

    fun onRecipeClicked(taskId: Long) {
        _navigateToRecipe.value = taskId
    }

    fun onRecipeNavigated() {
        _navigateToRecipe.value = null
    }
}