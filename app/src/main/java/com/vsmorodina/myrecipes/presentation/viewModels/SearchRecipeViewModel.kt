package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vsmorodina.myrecipes.data.entity.RecipeEntity
import com.vsmorodina.myrecipes.domain.useCase.SearchRecipesUseCase
import javax.inject.Inject

class SearchRecipeViewModel @Inject constructor(
    private val searchRecipesUseCase: SearchRecipesUseCase
) : ViewModel() {
    private var query: String? = null
    private var _recipesLiveData: LiveData<List<RecipeEntity>> = MutableLiveData()

    fun init(query: String) {
        this.query = query
    }

    fun getRecipes(): LiveData<List<RecipeEntity>>? {
        return query?.let {
            _recipesLiveData = searchRecipesUseCase.invoke(it)
            _recipesLiveData
        }
    }

}