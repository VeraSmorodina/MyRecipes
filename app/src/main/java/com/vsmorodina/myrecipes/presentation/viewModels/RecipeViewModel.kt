package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsmorodina.myrecipes.data.dao.RecipeDao
import com.vsmorodina.myrecipes.domain.useCase.DeleteRecipeUseCase
import com.vsmorodina.myrecipes.domain.useCase.GetRecipeUseCase
import com.vsmorodina.myrecipes.domain.useCase.UpdateFavoriteUseCase
import kotlinx.coroutines.launch

class RecipeViewModel(
    private val recipeId: Long,
    private val getRecipeUseCase: GetRecipeUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase
) : ViewModel() {
    val recipeLiveData = getRecipeUseCase.invoke(recipeId)

    private val _deleteRecipeCompletedLiveData = MutableLiveData<Unit>()
    val deleteRecipeCompletedLiveData: LiveData<Unit> = _deleteRecipeCompletedLiveData

    private val _isFavoriteLiveData = MutableLiveData<Boolean>()
    val isFavoriteLiveData: LiveData<Boolean> = _isFavoriteLiveData

    fun deleteRecipe() {
        recipeLiveData.value?.let {
            viewModelScope.launch {
                deleteRecipeUseCase.invoke(it.id)
                _deleteRecipeCompletedLiveData.value = Unit
            }
        }
    }

    fun updateFavorite() {
        viewModelScope.launch {
            val updated = !(_isFavoriteLiveData.value ?: false)
            updateFavoriteUseCase.invoke(recipeId, updated)
            _isFavoriteLiveData.value = updated
        }
    }

    fun getFavoriteStatus() {
        TODO("Исправить, при выходе из рецепта лайк не сохраняется")
        _isFavoriteLiveData.value = getRecipeUseCase.invoke(recipeId).value?.isFavorite ?: false
    }

    fun getRecipeInfo() = recipeLiveData.value?.getRecipeInfo()

}