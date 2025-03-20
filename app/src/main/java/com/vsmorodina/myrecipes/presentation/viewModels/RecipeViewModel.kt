package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsmorodina.myrecipes.data.dao.RecipeDao
import kotlinx.coroutines.launch

class RecipeViewModel(private val recipeId: Long, private val dao: RecipeDao) : ViewModel() {
    val recipeLiveData = dao.getRecipeLiveData(recipeId)
    private val _deleteRecipeCompletedLiveData = MutableLiveData<Unit>()
    val deleteRecipeCompletedLiveData: LiveData<Unit> = _deleteRecipeCompletedLiveData

    fun deleteRecipe() {
        recipeLiveData.value?.let {
            viewModelScope.launch {
                dao.delete(it)
                _deleteRecipeCompletedLiveData.value = Unit
            }
        }
    }
}