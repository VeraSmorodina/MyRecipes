package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vsmorodina.myrecipes.data.dao.CategoryDao
import com.vsmorodina.myrecipes.data.dao.RecipeDao
import com.vsmorodina.myrecipes.domain.useCase.GetRecipesUseCase

class RecipesViewModelFactory(private val categoryId: Long, private val useCase: GetRecipesUseCase): ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipesViewModel::class.java)) {
            return RecipesViewModel(useCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
