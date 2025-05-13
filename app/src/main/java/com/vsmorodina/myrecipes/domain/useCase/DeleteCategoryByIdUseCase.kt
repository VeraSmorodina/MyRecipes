package com.vsmorodina.myrecipes.domain.useCase

import com.vsmorodina.myrecipes.domain.repository.CategoryRepository
import javax.inject.Inject

class DeleteCategoryByIdUseCase @Inject constructor(private val categoryRepository: CategoryRepository) {
    suspend fun invoke(categoryId: Long) = categoryRepository.deleteCategoryById(categoryId)
}