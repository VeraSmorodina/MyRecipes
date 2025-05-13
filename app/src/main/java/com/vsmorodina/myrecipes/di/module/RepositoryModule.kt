package com.vsmorodina.myrecipes.di.module

import com.vsmorodina.myrecipes.data.repository.CategoryRepositoryImpl
import com.vsmorodina.myrecipes.data.repository.RecipesRepositoryImpl
import com.vsmorodina.myrecipes.domain.repository.CategoryRepository
import com.vsmorodina.myrecipes.domain.repository.RecipesRepository
import dagger.Binds
import dagger.Module

@Module
abstract class RepositoryModule {
    @Binds
    abstract fun provideRecipesRepository(recipesRepositoryImpl: RecipesRepositoryImpl): RecipesRepository

    @Binds
    abstract fun provideCategoryRepository(categoryRepositoryImpl: CategoryRepositoryImpl): CategoryRepository

}