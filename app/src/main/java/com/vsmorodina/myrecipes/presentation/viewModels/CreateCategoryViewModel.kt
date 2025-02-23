package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsmorodina.myrecipes.data.dao.CategoryDao
import com.vsmorodina.myrecipes.data.entity.CategoryEntity
import kotlinx.coroutines.launch

class CreateCategoryViewModel(private val categoryDao: CategoryDao): ViewModel() {
    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = _errorLiveData

    fun createCategory(name: String) {
        if (name.isBlank()) {
            _errorLiveData.value =
                "Не удалось сохранить категорию, название рецепта не может быть пустым"
            return
        }

        viewModelScope.launch {
            categoryDao.insert(
                CategoryEntity(
                    name = name,
                    photoUrl = "",
                )
            )
        }
    }



}