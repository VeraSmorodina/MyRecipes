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

class CreateRecipeViewModel(
    categoryDao: CategoryDao,
    private val recipeDao: RecipeDao
) : ViewModel() {
    val categories = categoryDao.getAll()
    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = _errorLiveData

    fun createRecipe(
        selectedCategoryIndex: Int,
        name: String,
        ingredients: String,
        cookingAlgorithm: String
    ) {
        val categoriesList = categories.value ?: return

        if (validateParameters(selectedCategoryIndex, categoriesList)) {
            _errorLiveData.value = "Не удалось сохранить рецепт"
            return
        }
        viewModelScope.launch {
            recipeDao.insert(
                RecipeEntity(
                    categoryId = categoriesList[selectedCategoryIndex].id,
                    name = name,
                    ingredients = ingredients,
                    cookingAlgorithm = cookingAlgorithm,
                    photoUrl = "",
                )
            )
        }
    }

    private fun validateParameters(selectedCategoryIndex: Int, categoriesList: List<CategoryEntity>) =
        selectedCategoryIndex < 0 || categoriesList.isEmpty()

}