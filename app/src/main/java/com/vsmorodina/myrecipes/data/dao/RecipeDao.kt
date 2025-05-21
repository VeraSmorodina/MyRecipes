package com.vsmorodina.myrecipes.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vsmorodina.myrecipes.data.entity.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipeEntity: RecipeEntity)

    @Delete
    suspend fun delete(recipeEntity: RecipeEntity)

    @Query("DELETE FROM recipes WHERE id = :recipeId")
    suspend fun deleteById(recipeId: Long)

    @Query("SELECT * FROM recipes WHERE id = :id")
    fun getRecipeLiveData(id: Long): LiveData<RecipeEntity>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipe(id: Long): RecipeEntity

    @Query("SELECT * FROM recipes WHERE category_id = :categoryId")
    fun getAllLiveData(categoryId: Long): LiveData<List<RecipeEntity>>

     @Query("SELECT * FROM recipes WHERE category_id = :categoryId")
    fun getAllFlow(categoryId: Long): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE is_favorite = 1")
    fun getFavouritesRecipe(): LiveData<List<RecipeEntity>>

    @Query("UPDATE recipes SET is_favorite = :isFavorite WHERE id = :recipeId")
    suspend fun updateFavorite(recipeId: Long, isFavorite: Boolean)

    @Query("SELECT * FROM recipes WHERE name LIKE :query")
    fun searchRecipes(query: String):  LiveData<List<RecipeEntity>>
}