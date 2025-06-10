package com.vsmorodina.myrecipes.domain.repository

import androidx.lifecycle.LiveData
import com.vsmorodina.myrecipes.data.entity.RecipeEntity
import com.vsmorodina.myrecipes.domain.entity.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipesRepository {
    fun getRecipesByCategory(categoryId: Long): Flow<List<Recipe>>
    suspend fun getRecipe(recipeId: Long): Recipe
    suspend fun deleteRecipe(recipeId: Long)
    suspend fun updateFavorite(recipeId: Long, isFavorite: Boolean)
    suspend fun recipeInsert(recipeEntity: RecipeEntity)
    fun getFavouritesRecipe(): LiveData<List<RecipeEntity>>
    fun searchRecipes(query: String): LiveData<List<RecipeEntity>>
}