package com.vsmorodina.myrecipes.di

import com.vsmorodina.myrecipes.di.module.AppModule
import com.vsmorodina.myrecipes.di.module.DatabaseModule
import com.vsmorodina.myrecipes.di.module.RepositoryModule
import com.vsmorodina.myrecipes.di.module.ViewModelModule
import com.vsmorodina.myrecipes.presentation.fragments.CategoriesFragment
import com.vsmorodina.myrecipes.presentation.fragments.ChangeCategoryFragment
import com.vsmorodina.myrecipes.presentation.fragments.ChangeRecipeFragment
import com.vsmorodina.myrecipes.presentation.fragments.CreateCategoryFragment
import com.vsmorodina.myrecipes.presentation.fragments.CreateRecipeFragment
import com.vsmorodina.myrecipes.presentation.fragments.FavouritesRecipesFragment
import com.vsmorodina.myrecipes.presentation.fragments.RecipeFragment
import com.vsmorodina.myrecipes.presentation.fragments.RecipesFragment
import com.vsmorodina.myrecipes.presentation.fragments.SearchRecipeFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class, AppModule::class, RepositoryModule::class, ViewModelModule::class])
interface ApplicationComponent {
    fun inject(recipesFragment: RecipesFragment)

    fun inject(recipeFragment: RecipeFragment)

    fun inject(categoriesFragment: CategoriesFragment)

    fun inject(createRecipeFragment: CreateRecipeFragment)

    fun inject(createCategoryFragment: CreateCategoryFragment)

    fun inject(favouritesRecipesFragment: FavouritesRecipesFragment)

    fun inject(changeCategoryFragment: ChangeCategoryFragment)

    fun inject(searchRecipeFragment: SearchRecipeFragment)

    fun inject(changeRecipeFragment: ChangeRecipeFragment)
}