package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsmorodina.myrecipes.data.entity.CategoryEntity
import com.vsmorodina.myrecipes.domain.entity.Category
import com.vsmorodina.myrecipes.domain.useCase.GetCategoryIdUseCase
import com.vsmorodina.myrecipes.domain.useCase.UpdateCategoryUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChangeCategoryViewModel @Inject constructor(
    private val getCategoryIdUseCase: GetCategoryIdUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase
) : ViewModel() {
    private var categoryId: Long? = null

    private var _categoryLiveData: LiveData<Category> = MutableLiveData()
    val categoryLiveData: LiveData<Category> = _categoryLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = _errorLiveData

    private val _imagePathLiveData = MutableLiveData<String>()
    val imagePathLiveData: LiveData<String> = _imagePathLiveData

    private var _successSavingCategoryLiveData = MutableLiveData<String?>()
    val successSavingCategoryLiveData: LiveData<String?> = _successSavingCategoryLiveData


    fun init(categoryId: Long) {
        this.categoryId = categoryId
    }

    fun getCategory(): LiveData<Category>? {
        return categoryId?.let {
            _categoryLiveData = getCategoryIdUseCase.invoke(it)
            _categoryLiveData
        }
    }

    fun createCategory(name: String) {
        if (name.isBlank()) {
            _errorLiveData.value =
                "Не удалось сохранить категорию, название не может быть пустым"
            return
        }

        viewModelScope.launch {
            categoryId?.let {
                updateCategoryUseCase.invoke(
                    CategoryEntity(
                        id = it,
                        name = name,
                        photoUri = when {
                            _imagePathLiveData.value != null -> _imagePathLiveData.value ?: ""
                            _imagePathLiveData.value == null -> categoryLiveData.value?.photoUri
                                ?: ""

                            else -> ""
                        },
                    )
                )
                _successSavingCategoryLiveData.value = "Категория сохранена успешно"
                _successSavingCategoryLiveData.value = null
            }
        }
    }

    fun saveImagePath(imagePath: String) {
        _imagePathLiveData.value = imagePath
    }
}