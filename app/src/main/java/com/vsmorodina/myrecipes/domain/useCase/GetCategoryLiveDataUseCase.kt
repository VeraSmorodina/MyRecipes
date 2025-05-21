package com.vsmorodina.myrecipes.domain.useCase

import com.vsmorodina.myrecipes.domain.repository.CategoryRepository
import javax.inject.Inject

class GetCategoryLiveDataUseCase @Inject constructor(private val categoryRepository: CategoryRepository) {
    fun invoke() = categoryRepository.getCategoriesLiveData()
}