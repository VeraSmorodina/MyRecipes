package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsmorodina.myrecipes.data.dao.CategoryDao
import com.vsmorodina.myrecipes.data.dao.RecipeDao
import com.vsmorodina.myrecipes.data.entity.CategoryEntity
import com.vsmorodina.myrecipes.data.entity.RecipeEntity
import kotlinx.coroutines.launch

class ChangeRecipeViewModel(
    val recipeId: Long,
    val recipeDao: RecipeDao,
    val categoryDao: CategoryDao
) : ViewModel() {
    val recipeLiveData = recipeDao.getRecipeLiveData(recipeId)
    val categories = categoryDao.getAllLiveData()

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = _errorLiveData

    private val _imagePathLiveData = MutableLiveData<String>()
    val imagePathLiveData: LiveData<String> = _imagePathLiveData

    private val _successSavingRecipeLiveData = MutableLiveData<String?>()
    val successSavingRecipeLiveData: LiveData<String?> = _successSavingRecipeLiveData

    private val _selectedCategoryIndexLiveData = MutableLiveData<Int>()
    val selectedCategoryIndexLiveData: LiveData<Int> = _selectedCategoryIndexLiveData


    fun changeRecipe(
        selectedCategoryIndex: Int,
        name: String,
        ingredients: String,
        cookingAlgorithm: String,
    ) {
        val categoriesList = categories.value ?: return

        if (validateParameters(selectedCategoryIndex, categoriesList)) {
            _errorLiveData.value = "Не удалось сохранить рецепт"
            return
        }

        if (name.isBlank()) {
            _errorLiveData.value =
                "Не удалось сохранить категорию, название не может быть пустым"
            return
        }
        viewModelScope.launch {
            recipeDao.insert(
                RecipeEntity(
                    id = recipeId,
                    categoryId = categoriesList[selectedCategoryIndex].id,
                    name = name,
                    ingredients = ingredients,
                    cookingAlgorithm = cookingAlgorithm,
                    photoUri = when {
                        _imagePathLiveData.value != null ->  _imagePathLiveData.value ?: ""
                        _imagePathLiveData.value == null -> recipeLiveData.value?.photoUri ?: ""
                        else -> ""
                    },
                )
            )
        }
        _successSavingRecipeLiveData.value = "Cохранено успешно"
        _successSavingRecipeLiveData.value = null
    }

    private fun validateParameters(
        selectedCategoryIndex: Int,
        categoriesList: List<CategoryEntity>
    ) =
        selectedCategoryIndex < 0 || categoriesList.isEmpty()

    fun saveImagePath(imagePath: String) {
        _imagePathLiveData.value = imagePath
    }

    fun setCategoryIndex(index: Int) {
        _selectedCategoryIndexLiveData.value = index
    }

    fun calculateCategoryIndex() {
        viewModelScope.launch {
            val categoryId = recipeDao.getRecipe(recipeId).categoryId
            val category = categoryDao.getCategory(categoryId)
            val categoriesList = categoryDao.getAll()
            _selectedCategoryIndexLiveData.value = categoriesList.indexOf(category)
        }
    }
}