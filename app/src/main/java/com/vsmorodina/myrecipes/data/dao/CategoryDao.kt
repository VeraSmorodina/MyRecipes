package com.vsmorodina.myrecipes.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.vsmorodina.myrecipes.data.entity.CategoryEntity

@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(categoryEntity: CategoryEntity)

    @Update
    suspend fun update(categoryEntity: CategoryEntity)

    @Delete
    suspend fun delete(categoryEntity: CategoryEntity)

    @Query("SELECT * FROM categories WHERE id = :id")
    fun get(id: Long): LiveData<CategoryEntity>

    @Query("SELECT * FROM categories")
    fun getAll(): LiveData<List<CategoryEntity>>
}