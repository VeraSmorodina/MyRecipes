package com.vsmorodina.myrecipes.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.vsmorodina.myrecipes.data.entity.CategoryEntity
import com.vsmorodina.myrecipes.data.entity.RecipePhotoEntity

@Dao
interface RecipePhotoDao {
    @Insert
    suspend fun insert(recipePhotoEntity: RecipePhotoEntity)

    @Update
    suspend fun update(recipePhotoEntity: RecipePhotoEntity)

    @Delete
    suspend fun delete(recipePhotoEntity: RecipePhotoEntity)

    @Query("SELECT * FROM recipe_photos WHERE recipe_id = :recipeId")
    fun getAll(recipeId: Long): LiveData<List<RecipePhotoEntity>>
}