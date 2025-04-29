package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vsmorodina.myrecipes.data.dao.RecipeDao
import com.vsmorodina.myrecipes.domain.useCase.DeleteRecipeUseCase
import com.vsmorodina.myrecipes.domain.useCase.GetRecipeUseCase
import com.vsmorodina.myrecipes.domain.useCase.UpdateFavoriteUseCase

class RecipeViewModelFactory(
    private val recipeId: Long,
    private val getRecipeUseCase: GetRecipeUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            return RecipeViewModel(
                recipeId,
                getRecipeUseCase,
                deleteRecipeUseCase,
                updateFavoriteUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}