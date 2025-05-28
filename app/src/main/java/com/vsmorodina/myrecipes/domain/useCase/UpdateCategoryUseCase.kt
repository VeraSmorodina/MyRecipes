package com.vsmorodina.myrecipes.domain.useCase

import com.vsmorodina.myrecipes.data.entity.CategoryEntity
import com.vsmorodina.myrecipes.domain.repository.CategoryRepository
import javax.inject.Inject

class UpdateCategoryUseCase @Inject constructor(private val categoryRepository: CategoryRepository) {
    suspend fun invoke(categoryEntity: CategoryEntity) = categoryRepository.updateCategory(categoryEntity)
}