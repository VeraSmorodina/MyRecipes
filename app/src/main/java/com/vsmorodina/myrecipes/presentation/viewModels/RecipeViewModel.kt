package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsmorodina.myrecipes.domain.entity.Recipe
import com.vsmorodina.myrecipes.domain.useCase.DeleteRecipeUseCase
import com.vsmorodina.myrecipes.domain.useCase.GetRecipeUseCase
import com.vsmorodina.myrecipes.domain.useCase.UpdateFavoriteUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class RecipeViewModel @Inject constructor(
    private val getRecipeUseCase: GetRecipeUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase
) : ViewModel() {
    private var recipeId: Long? = null

    private var _recipeLiveData: LiveData<Recipe> = MutableLiveData()
    val recipeLiveData: LiveData<Recipe> = _recipeLiveData

    private val _deleteRecipeCompletedLiveData = MutableLiveData<Unit>()
    val deleteRecipeCompletedLiveData: LiveData<Unit> = _deleteRecipeCompletedLiveData

    private val _isFavoriteLiveData = MutableLiveData<Boolean>()
    val isFavoriteLiveData: LiveData<Boolean> = _isFavoriteLiveData

    fun init(recipeId: Long) {
        this.recipeId = recipeId
    }

    fun getRecipe() {
        recipeId?.let {
            _recipeLiveData = getRecipeUseCase.invoke(it)
        }
    }

    fun deleteRecipe() {
        recipeLiveData.value?.let {
            viewModelScope.launch {
                deleteRecipeUseCase.invoke(it.id)
                _deleteRecipeCompletedLiveData.value = Unit
            }
        }
    }

    fun updateFavorite() {
        recipeId?.let {
            viewModelScope.launch {
                val updated = !(_isFavoriteLiveData.value ?: false)
                updateFavoriteUseCase.invoke(it, updated)
                _isFavoriteLiveData.value = updated
            }
        }
    }

    //    TODO("Исправить, при выходе из рецепта лайк не сохраняется")
    fun getFavoriteStatus() {
        recipeId?.let {
            _isFavoriteLiveData.value = getRecipeUseCase.invoke(it).value?.isFavorite ?: false
        }
    }

    fun getRecipeInfo() = recipeLiveData.value?.getRecipeInfo()

}