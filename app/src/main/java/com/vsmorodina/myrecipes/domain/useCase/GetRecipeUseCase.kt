package com.vsmorodina.myrecipes.domain.useCase

import com.vsmorodina.myrecipes.domain.repository.RecipesRepository
import javax.inject.Inject

class GetRecipeUseCase@Inject constructor(private val recipesRepository: RecipesRepository) {
    fun invoke(recipeId: Long) = recipesRepository.getRecipe(recipeId)
}