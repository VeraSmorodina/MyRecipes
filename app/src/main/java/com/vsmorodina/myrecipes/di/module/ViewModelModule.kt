package com.vsmorodina.myrecipes.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vsmorodina.myrecipes.di.AppViewModelFactory
import com.vsmorodina.myrecipes.di.annotation.ViewModelKey
import com.vsmorodina.myrecipes.presentation.viewModels.CategoriesViewModel
import com.vsmorodina.myrecipes.presentation.viewModels.ChangeCategoryViewModel
import com.vsmorodina.myrecipes.presentation.viewModels.ChangeRecipeViewModel
import com.vsmorodina.myrecipes.presentation.viewModels.CreateCategoryViewModel
import com.vsmorodina.myrecipes.presentation.viewModels.CreateRecipeViewModel
import com.vsmorodina.myrecipes.presentation.viewModels.FavouritesRecipesViewModel
import com.vsmorodina.myrecipes.presentation.viewModels.RecipeViewModel
import com.vsmorodina.myrecipes.presentation.viewModels.RecipesViewModel
import com.vsmorodina.myrecipes.presentation.viewModels.SearchRecipeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @IntoMap
    @ViewModelKey(CategoriesViewModel::class)
    @Binds
    abstract fun provideCategoriesViewModel(categoriesViewModel: CategoriesViewModel): ViewModel

    @IntoMap
    @ViewModelKey(RecipesViewModel::class)
    @Binds
    abstract fun provideRecipesViewModel(recipesViewModel: RecipesViewModel): ViewModel

    @IntoMap
    @ViewModelKey(RecipeViewModel::class)
    @Binds
    abstract fun provideRecipeViewModel(recipeViewModel: RecipeViewModel): ViewModel

    @IntoMap
    @ViewModelKey(CreateRecipeViewModel::class)
    @Binds
    abstract fun provideCreateRecipeViewModel(createRecipeViewModel: CreateRecipeViewModel): ViewModel

    @IntoMap
    @ViewModelKey(CreateCategoryViewModel::class)
    @Binds
    abstract fun provideCreateCategoryViewModel(createCategoryViewModel: CreateCategoryViewModel): ViewModel

    @IntoMap
    @ViewModelKey(FavouritesRecipesViewModel::class)
    @Binds
    abstract fun provideFavouritesRecipesViewModel(favouritesRecipesViewModel: FavouritesRecipesViewModel): ViewModel

    @IntoMap
    @ViewModelKey(ChangeCategoryViewModel::class)
    @Binds
    abstract fun provideChangeCategoryViewModel(changeCategoryViewModel: ChangeCategoryViewModel): ViewModel

    @IntoMap
    @ViewModelKey(SearchRecipeViewModel::class)
    @Binds
    abstract fun provideSearchRecipeViewModel(searchRecipeViewModel: SearchRecipeViewModel): ViewModel

    @IntoMap
    @ViewModelKey(ChangeRecipeViewModel::class)
    @Binds
    abstract fun provideChangeRecipeViewModel(changeRecipeViewModel: ChangeRecipeViewModel): ViewModel

    @Binds
    abstract fun provideAppViewModelFactory(appViewModelFactory: AppViewModelFactory): ViewModelProvider.Factory
}