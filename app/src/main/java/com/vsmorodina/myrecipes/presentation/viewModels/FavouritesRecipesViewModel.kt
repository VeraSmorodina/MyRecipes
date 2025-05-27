package com.vsmorodina.myrecipes.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.vsmorodina.myrecipes.data.dao.RecipeDao
import com.vsmorodina.myrecipes.domain.useCase.GetFavouriteRecipesUseCase
import javax.inject.Inject

class FavouritesRecipesViewModel @Inject constructor(
//    val dao: RecipeDao
    favouriteRecipesUseCase: GetFavouriteRecipesUseCase
) : ViewModel() {
    val recipesLiveData = favouriteRecipesUseCase.invoke()
}