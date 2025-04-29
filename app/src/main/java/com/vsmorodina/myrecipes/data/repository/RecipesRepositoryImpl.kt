package com.vsmorodina.myrecipes.data.repository

import com.vsmorodina.myrecipes.data.dao.RecipeDao
import com.vsmorodina.myrecipes.domain.entity.Recipe
import com.vsmorodina.myrecipes.domain.repository.RecipesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecipesRepositoryImpl @Inject constructor(private val recipeDao: RecipeDao) :
    RecipesRepository {
    override fun getRecipesByCategory(categoryId: Long): Flow<List<Recipe>> {
        return recipeDao.getAllFlow(categoryId).map {
            it.map {
                Recipe(
                    id = it.id,
                    categoryId = it.categoryId,
                    name = it.name,
                    ingredients = it.ingredients,
                    cookingAlgorithm = it.cookingAlgorithm,
                    photoUri = it.photoUri,
                    isFavorite = it.isFavorite
                )
            }
        }
    }

}