package com.vsmorodina.myrecipes.domain.useCase

import com.vsmorodina.myrecipes.domain.repository.RecipesRepository
import javax.inject.Inject

class GetRecipesUseCase @Inject constructor(private val recipesRepository: RecipesRepository) {
    fun invoke(categoryId: Long) = recipesRepository.getRecipesByCategory(categoryId)
}