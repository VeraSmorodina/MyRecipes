package com.vsmorodina.myrecipes.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vsmorodina.myrecipes.di.AppViewModelFactory
import com.vsmorodina.myrecipes.di.annotation.ViewModelKey
import com.vsmorodina.myrecipes.domain.useCase.DeleteCategoryByIdUseCase
import com.vsmorodina.myrecipes.domain.useCase.GetCategoriesUseCase
import com.vsmorodina.myrecipes.domain.useCase.GetRecipesUseCase
import com.vsmorodina.myrecipes.presentation.viewModels.CategoriesViewModel
import com.vsmorodina.myrecipes.presentation.viewModels.RecipesViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.internal.Provider
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

    @Binds
    abstract fun provideAppViewModelFactory(appViewModelFactory: AppViewModelFactory): ViewModelProvider.Factory


}