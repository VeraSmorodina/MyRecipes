package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsmorodina.myrecipes.data.dao.RecipeDao
import kotlinx.coroutines.launch

class RecipeViewModel(private val recipeId: Long, private val recipeDao: RecipeDao) : ViewModel() {
    val recipeLiveData = recipeDao.getRecipeLiveData(recipeId)

    private val _deleteRecipeCompletedLiveData = MutableLiveData<Unit>()
    val deleteRecipeCompletedLiveData: LiveData<Unit> = _deleteRecipeCompletedLiveData

    private val _isFavoriteLiveData = MutableLiveData<Boolean>()
    val isFavoriteLiveData: LiveData<Boolean> = _isFavoriteLiveData

    fun deleteRecipe() {
        recipeLiveData.value?.let {
            viewModelScope.launch {
                recipeDao.delete(it)
                _deleteRecipeCompletedLiveData.value = Unit
            }
        }
    }

    fun updateFavorite() {
        viewModelScope.launch {
            val updated = !(_isFavoriteLiveData.value ?: false)
            recipeDao.updateFavorite(recipeId, updated)
            _isFavoriteLiveData.value = updated
        }
    }

    fun getFavoriteStatus(){
        viewModelScope.launch {
            _isFavoriteLiveData.value = recipeDao.getRecipe(recipeId).isFavorite
        }
    }

    fun getRecipeInfo() = recipeLiveData.value?.getRecipeInfo()

}