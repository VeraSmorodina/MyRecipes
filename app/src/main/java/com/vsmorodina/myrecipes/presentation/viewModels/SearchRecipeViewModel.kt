package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsmorodina.myrecipes.data.dao.RecipeDao
import com.vsmorodina.myrecipes.data.entity.RecipeEntity
import com.vsmorodina.myrecipes.domain.entity.Recipe
import com.vsmorodina.myrecipes.domain.useCase.SearchRecipesUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchRecipeViewModel @Inject constructor(
//    query: String,
//    val dao: RecipeDao
    private val searchRecipesUseCase: SearchRecipesUseCase
) : ViewModel() {
    private var query: String? = null

//    private var _recipesLiveData: LiveData<Recipe> = MutableLiveData()
    private var _recipesLiveData: LiveData<List<RecipeEntity>> = MutableLiveData()
    val recipesLiveData: LiveData<List<RecipeEntity>> = _recipesLiveData


//    val recipesLiveData = dao.searchRecipes(query)

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