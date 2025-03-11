package com.vsmorodina.myrecipes.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.vsmorodina.myrecipes.data.entity.RecipeEntity

@Dao
interface RecipeDao {
    @Insert
    suspend fun insert(recipeEntity: RecipeEntity)

    @Update
    suspend fun update(recipeEntity: RecipeEntity)

    @Delete
    suspend fun delete(recipeEntity: RecipeEntity)

    @Query("SELECT * FROM recipes WHERE id = :id")
    fun get(id: Long): LiveData<RecipeEntity>

    @Query("SELECT * FROM recipes WHERE category_id = :categoryId")
    fun getAll(categoryId: Long): LiveData<List<RecipeEntity>>
}