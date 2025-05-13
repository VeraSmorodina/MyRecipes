package com.vsmorodina.myrecipes.domain.repository

import com.vsmorodina.myrecipes.domain.entity.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(): Flow<List<Category>>
    suspend fun deleteCategoryById(categoryId: Long)
}