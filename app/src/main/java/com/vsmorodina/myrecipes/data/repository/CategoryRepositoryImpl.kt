package com.vsmorodina.myrecipes.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.vsmorodina.myrecipes.data.dao.CategoryDao
import com.vsmorodina.myrecipes.data.entity.CategoryEntity
import com.vsmorodina.myrecipes.domain.entity.Category
import com.vsmorodina.myrecipes.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(private val categoryDao: CategoryDao) :
    CategoryRepository {
    override fun getCategoriesLiveData(): LiveData<List<CategoryEntity>> {
        return categoryDao.getAllLiveData()
    }

    override fun getCategories(): Flow<List<Category>> {
        return categoryDao.getAllFlow().map {
            it.map {
                it.toModel()
            }
        }
    }

    override suspend fun deleteCategoryById(categoryId: Long) = categoryDao.deleteById(categoryId)

    override suspend fun insertCategory(categoryEntity: CategoryEntity) =
        categoryDao.insert(categoryEntity)

    override fun getCategoryLiveData(categoryId: Long): LiveData<Category> {
        return categoryDao.getCategoryLiveData(categoryId).map { it.toModel() }
    }

    override suspend fun updateCategory(categoryEntity: CategoryEntity) {
        categoryDao.update(categoryEntity)
    }
}