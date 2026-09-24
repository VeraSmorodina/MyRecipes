package com.vsmorodina.myrecipes.di.module

import android.app.Application
import com.vsmorodina.myrecipes.data.AppDatabase
import com.vsmorodina.myrecipes.data.dao.CategoryDao
import com.vsmorodina.myrecipes.data.dao.RecipeDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    // Тот же экземпляр, что использует RecipesApplication: миграции и настройки базы заданы в одном месте
    @Provides
    @Singleton
    fun provideAppDatabase(applicationContext: Application): AppDatabase =
        AppDatabase.getInstance(applicationContext)

    @Provides
    fun provideRecipeDao(appDatabase: AppDatabase): RecipeDao = appDatabase.recipeDao

    @Provides
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao = appDatabase.categoryDao
}
