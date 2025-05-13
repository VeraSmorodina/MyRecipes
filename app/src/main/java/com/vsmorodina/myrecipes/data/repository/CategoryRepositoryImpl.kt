package com.vsmorodina.myrecipes.data.repository

import androidx.lifecycle.map
import com.vsmorodina.myrecipes.data.dao.CategoryDao
import com.vsmorodina.myrecipes.domain.entity.Category
import com.vsmorodina.myrecipes.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(private val categoryDao: CategoryDao) :
    CategoryRepository {
    override fun getCategories(): Flow<List<Category>> {
        return categoryDao.getAllFlow().map {
            it.map {
                it.toModel()
            }
        }
    }

    override suspend fun deleteCategoryById(categoryId: Long) = categoryDao.deleteById(categoryId)
}