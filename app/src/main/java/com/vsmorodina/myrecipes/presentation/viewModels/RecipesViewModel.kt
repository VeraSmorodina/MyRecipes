package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.vsmorodina.myrecipes.domain.useCase.GetRecipesUseCase

class RecipesViewModel(categoryId: Long, val useCase: GetRecipesUseCase) : ViewModel() {
    val recipesFlow = useCase.invoke(categoryId)
}