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

sealed interface DisplayMessage {
    data class ToastMessage(val message: String) : DisplayMessage
    data class AlertDialogMessage(val message: String) : DisplayMessage
}

class CreateRecipeViewModel(
    categoryDao: CategoryDao,
    private val recipeDao: RecipeDao
) : ViewModel() {
    val categories = categoryDao.getAllLiveData()

    private val _errorLiveData = MutableLiveData<DisplayMessage?>()
    val errorLiveData: LiveData<DisplayMessage?> = _errorLiveData

    private var _imagePathLiveData = MutableLiveData<String>()
    val imagePathLiveData: LiveData<String> = _imagePathLiveData

    private var _successSavingCategoryLiveData = MutableLiveData<String?>()
    val successSavingCategoryLiveData: LiveData<String?> = _successSavingCategoryLiveData

    fun createRecipe(
        selectedCategoryIndex: Int,
        name: String,
        ingredients: String,
        cookingAlgorithm: String,
    ) {
        val categoriesList = categories.value ?: return

        if (categoriesList.isEmpty()) {
            _errorLiveData.value = DisplayMessage.AlertDialogMessage("Для сохранения рецепта требуется указать категорию. Список категорий пуст. Создать категорию?")
            _errorLiveData.value = null
            return
        }

        if (validateParameters(selectedCategoryIndex, categoriesList)) {
            _errorLiveData.value = DisplayMessage.ToastMessage("Не удалось сохранить рецепт")
            return
        }

        if (name.isBlank()) {
            _errorLiveData.value = DisplayMessage.ToastMessage(
                "Не удалось сохранить рецепт, название не может быть пустым"
            )
            return
        }
        viewModelScope.launch {
            recipeDao.insert(
                RecipeEntity(
                    categoryId = categoriesList[selectedCategoryIndex].id,
                    name = name,
                    ingredients = ingredients,
                    cookingAlgorithm = cookingAlgorithm,
                    photoUri = _imagePathLiveData.value ?: "",
                )
            )
        }
        _successSavingCategoryLiveData.value = "Cохранено успешно"
        _successSavingCategoryLiveData.value = null
    }

    private fun validateParameters(
        selectedCategoryIndex: Int,
        categoriesList: List<CategoryEntity>
    ) =
        selectedCategoryIndex < 0 || categoriesList.isEmpty()

    fun saveImagePath(imagePath: String) {
        _imagePathLiveData.value = imagePath
    }
}