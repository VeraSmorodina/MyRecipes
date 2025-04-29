package com.vsmorodina.myrecipes.di

import com.vsmorodina.myrecipes.di.module.AppModule
import com.vsmorodina.myrecipes.di.module.DatabaseModule
import com.vsmorodina.myrecipes.di.module.RepositoryModule
import com.vsmorodina.myrecipes.presentation.fragments.RecipeFragment
import com.vsmorodina.myrecipes.presentation.fragments.RecipesFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class, AppModule::class, RepositoryModule::class])
interface ApplicationComponent {
    fun inject(recipesFragment: RecipesFragment)

    fun inject(recipeFragment: RecipeFragment)
}