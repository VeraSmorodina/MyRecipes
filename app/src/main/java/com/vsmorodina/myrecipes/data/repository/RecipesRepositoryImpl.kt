package com.vsmorodina.myrecipes.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.vsmorodina.myrecipes.data.dao.RecipeDao
import com.vsmorodina.myrecipes.data.entity.RecipeEntity
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

    override suspend fun getRecipe(recipeId: Long): Recipe {
        return recipeDao.getRecipe(recipeId).toModel()
    }

    override suspend fun deleteRecipe(recipeId: Long) {
        return recipeDao.deleteById(recipeId)
    }

    override suspend fun updateFavorite(recipeId: Long, isFavorite: Boolean) {
        return recipeDao.updateFavorite(recipeId, isFavorite)
    }

    override suspend fun recipeInsert(recipeEntity: RecipeEntity) {
        recipeDao.insert(recipeEntity)
    }

    override fun getFavouritesRecipe(): LiveData<List<RecipeEntity>> {
        return recipeDao.getFavouritesRecipe()
    }

    override fun searchRecipes(query: String): LiveData<List<RecipeEntity>> {
        return recipeDao.getAllRecipesLiveData().map { filterByName(it, query) }
    }

    companion object {
        /**
         * Рецепты, в названии которых встречается [query] (частичное совпадение, без учёта регистра).
         * Фильтруем в Kotlin, а не через LIKE: в SQLite на Android LIKE и lower() не учитывают
         * регистр только для латиницы, и «борщ» не нашёл бы «Борщ».
         */
        fun filterByName(recipes: List<RecipeEntity>, query: String): List<RecipeEntity> {
            val trimmedQuery = query.trim()
            if (trimmedQuery.isEmpty()) return emptyList()
            return recipes.filter { it.name.contains(trimmedQuery, ignoreCase = true) }
        }
    }
}