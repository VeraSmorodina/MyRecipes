package com.vsmorodina.myrecipes.domain.repository

import androidx.lifecycle.LiveData
import com.vsmorodina.myrecipes.data.entity.CategoryEntity
import com.vsmorodina.myrecipes.domain.entity.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategoriesLiveData(): LiveData<List<CategoryEntity>>
    fun getCategories(): Flow<List<Category>>
    suspend fun deleteCategoryById(categoryId: Long)
    suspend fun insertCategory(categoryEntity: CategoryEntity)
    fun getCategoryLiveData(categoryId: Long):LiveData<Category>
    suspend fun updateCategory(categoryEntity: CategoryEntity)
//    suspend fun updateCategory(category: Category)
}