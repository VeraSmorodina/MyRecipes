package com.vsmorodina.myrecipes.domain.repository

import com.vsmorodina.myrecipes.domain.entity.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipesRepository {
    fun getRecipesByCategory(categoryId: Long): Flow<List<Recipe>>
}