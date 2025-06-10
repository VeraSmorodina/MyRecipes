package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsmorodina.myrecipes.data.entity.RecipeEntity
import com.vsmorodina.myrecipes.domain.entity.Category
import com.vsmorodina.myrecipes.domain.entity.Recipe
import com.vsmorodina.myrecipes.domain.useCase.GetCategoriesUseCase
import com.vsmorodina.myrecipes.domain.useCase.GetCategoryIdUseCase
import com.vsmorodina.myrecipes.domain.useCase.GetRecipeUseCase
import com.vsmorodina.myrecipes.domain.useCase.InsertRecipeUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChangeRecipeViewModel @Inject constructor(
    private val getRecipeUseCase: GetRecipeUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val insertRecipeUseCase: InsertRecipeUseCase,
    private val getCategoryIdUseCase: GetCategoryIdUseCase
) : ViewModel() {
    private var recipeId: Long? = null

    private var _recipeLiveData = MutableLiveData<Recipe>()
    val recipeLiveData: LiveData<Recipe> = _recipeLiveData

    private var _categoriesFlow: MutableStateFlow<List<Category>> =
        MutableStateFlow(emptyList())
    val categoriesFlow: Flow<List<Category>> = _categoriesFlow

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = _errorLiveData

    private val _imagePathLiveData = MutableLiveData<String>()
    val imagePathLiveData: LiveData<String> = _imagePathLiveData

    private val _successSavingRecipeLiveData = MutableLiveData<String?>()
    val successSavingRecipeLiveData: LiveData<String?> = _successSavingRecipeLiveData

    private val _selectedCategoryIndexLiveData = MutableLiveData<Int>()
    val selectedCategoryIndexLiveData: LiveData<Int> = _selectedCategoryIndexLiveData


    fun initRecipe(recipeId: Long) {
        this.recipeId = recipeId
    }

    fun getRecipe() {
        recipeId?.let {
            viewModelScope.launch {
                _recipeLiveData.value = getRecipeUseCase.invoke(it)
            }
        }
    }

    fun getCategories() {
        viewModelScope.launch {
            getCategoriesUseCase.invoke().collectLatest {
                _categoriesFlow.emit(it)
            }
        }
    }

    fun changeRecipe(
        selectedCategoryIndex: Int,
        name: String,
        ingredients: String,
        cookingAlgorithm: String,
    ) {
        val categoriesList = _categoriesFlow.value

        if (validateParameters(selectedCategoryIndex, categoriesList)) {
            _errorLiveData.value = "Не удалось сохранить рецепт"
            return
        }

        if (name.isBlank()) {
            _errorLiveData.value =
                "Не удалось сохранить категорию, название не может быть пустым"
            return
        }

        recipeId?.let {
            viewModelScope.launch {
                insertRecipeUseCase.invoke(
                    RecipeEntity(
                        id = it,
                        categoryId = categoriesList[selectedCategoryIndex].id,
                        name = name,
                        ingredients = ingredients,
                        cookingAlgorithm = cookingAlgorithm,
                        photoUri = when {
                            _imagePathLiveData.value != null -> _imagePathLiveData.value ?: ""
                            _imagePathLiveData.value == null -> recipeLiveData.value?.photoUri ?: ""
                            else -> ""
                        },
                    )
                )
            }
        }
        _successSavingRecipeLiveData.value = "Cохранено успешно"
        _successSavingRecipeLiveData.value = null
    }

    private fun validateParameters(
        selectedCategoryIndex: Int,
        categoriesList: List<Category>
    ) =
        selectedCategoryIndex < 0 || categoriesList.isEmpty()

    fun saveImagePath(imagePath: String) {
        _imagePathLiveData.value = imagePath
    }

    fun calculateCategoryIndex() {
        recipeId?.let {
            viewModelScope.launch {
                val categoryId = _recipeLiveData.value?.categoryId
                categoryId?.let {
                    val category = getCategoryIdUseCase.invoke(it).value
                    val categoriesList = _categoriesFlow.value
                    _selectedCategoryIndexLiveData.value = categoriesList.indexOf(category)
                }
            }
        }
    }
}