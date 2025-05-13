package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsmorodina.myrecipes.domain.entity.Recipe
import com.vsmorodina.myrecipes.domain.useCase.GetRecipesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class RecipesViewModel @Inject constructor(private val useCase: GetRecipesUseCase) : ViewModel() {
    private val _recipesFlow: MutableStateFlow<List<Recipe>> = MutableStateFlow(emptyList())
    val recipesFlow: Flow<List<Recipe>> = _recipesFlow

    fun getRecipes(categoryId: Long) {
        viewModelScope.launch {
            useCase.invoke(categoryId).collectLatest {
                _recipesFlow.emit(it)
            }
        }
    }
}