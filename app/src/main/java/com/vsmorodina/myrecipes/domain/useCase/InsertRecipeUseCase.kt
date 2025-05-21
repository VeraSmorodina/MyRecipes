package com.vsmorodina.myrecipes.domain.useCase

import com.vsmorodina.myrecipes.data.entity.RecipeEntity
import com.vsmorodina.myrecipes.domain.repository.RecipesRepository
import javax.inject.Inject

class InsertRecipeUseCase @Inject constructor(private val recipesRepository: RecipesRepository) {
    suspend fun invoke(recipeEntity: RecipeEntity) = recipesRepository.recipeInsert(recipeEntity)
}