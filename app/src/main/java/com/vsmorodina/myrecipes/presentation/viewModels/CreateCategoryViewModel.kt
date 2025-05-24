package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsmorodina.myrecipes.data.dao.CategoryDao
import com.vsmorodina.myrecipes.data.entity.CategoryEntity
import com.vsmorodina.myrecipes.domain.useCase.InsertCategoryUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreateCategoryViewModel @Inject constructor(
//    private val categoryDao: CategoryDao
    private val insertCategoryUseCase: InsertCategoryUseCase
) :
    ViewModel() {
    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = _errorLiveData

    private var _imagePathLiveData = MutableLiveData<String>()
    val imagePathLiveData: LiveData<String> = _imagePathLiveData

    private var _successSavingCategoryLiveData = MutableLiveData<String?>()
    val successSavingCategoryLiveData: LiveData<String?> = _successSavingCategoryLiveData


    fun createCategory(name: String) {
        if (name.isBlank()) {
            _errorLiveData.value =
                "Не удалось сохранить категорию, название не может быть пустым"
            return
        }

        viewModelScope.launch {
            insertCategoryUseCase.invoke(
                CategoryEntity(
                    name = name,
                    photoUri = _imagePathLiveData.value ?: "",
                )
            )
//            categoryDao.insert(
//                CategoryEntity(
//                    name = name,
//                    photoUri = _imagePathLiveData.value ?: "",
//                )
//            )
            _successSavingCategoryLiveData.value = "Категория сохранена успешно"
            _successSavingCategoryLiveData.value = null
        }
    }

    fun saveImagePath(imagePath: String) {
        _imagePathLiveData.value = imagePath
    }
}