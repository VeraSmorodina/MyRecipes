package com.vsmorodina.myrecipes.domain.useCase

import com.vsmorodina.myrecipes.domain.repository.CategoryRepository
import javax.inject.Inject

class GetCategoryIdUseCase @Inject constructor(private val categoryRepository: CategoryRepository){
    fun invoke(categoryId: Long) = categoryRepository.getCategoryLiveData(categoryId)

}