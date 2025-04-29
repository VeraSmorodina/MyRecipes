package com.vsmorodina.myrecipes.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
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
                it.toModel()
            }
        }
    }

    override fun getRecipe(recipeId: Long): LiveData<Recipe> {
        return recipeDao.getRecipeLiveData(recipeId).map {
            it.toModel()
        }
    }

    override suspend fun deleteRecipe(recipeId: Long) {
        return recipeDao.deleteById(recipeId)
    }

    override suspend fun updateFavorite(recipeId: Long, isFavorite: Boolean) {
        return recipeDao.updateFavorite(recipeId, isFavorite)
    }

}