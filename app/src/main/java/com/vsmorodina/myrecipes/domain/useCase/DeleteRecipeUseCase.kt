package com.vsmorodina.myrecipes.domain.useCase

import com.vsmorodina.myrecipes.domain.repository.RecipesRepository
import javax.inject.Inject

class DeleteRecipeUseCase @Inject constructor(private val recipesRepository: RecipesRepository) {
    suspend fun invoke(recipeId: Long) = recipesRepository.deleteRecipe(recipeId)
}