package com.vsmorodina.myrecipes.domain.useCase

import com.vsmorodina.myrecipes.domain.repository.RecipesRepository
import javax.inject.Inject

class SearchRecipesUseCase @Inject constructor(private val recipesRepository: RecipesRepository) {
    fun invoke(query: String) = recipesRepository.searchRecipes(query)
}