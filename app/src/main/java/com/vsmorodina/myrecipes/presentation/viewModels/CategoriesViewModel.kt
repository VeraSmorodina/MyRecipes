package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsmorodina.myrecipes.domain.useCase.DeleteCategoryByIdUseCase
import com.vsmorodina.myrecipes.domain.useCase.GetCategoriesUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

//class CategoriesViewModel(val dao: CategoryDao) : ViewModel() {
//    val categoriesLiveData = dao.getAllLiveData()
//
//    fun deleteCategory(categoryId: Long){
//        viewModelScope.launch {
//            dao.deleteById(categoryId = categoryId)
//        }
//    }
//}

class CategoriesViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val deleteCategoryByIdUseCase: DeleteCategoryByIdUseCase
) : ViewModel() {
    val categoriesFlow = getCategoriesUseCase.invoke()

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            deleteCategoryByIdUseCase.invoke(categoryId = categoryId)
        }
    }
}