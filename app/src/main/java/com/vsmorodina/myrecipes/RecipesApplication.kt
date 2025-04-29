package com.vsmorodina.myrecipes

import android.app.Application
import android.content.Context
import com.vsmorodina.myrecipes.data.AppDatabase
import com.vsmorodina.myrecipes.data.entity.CategoryEntity
import com.vsmorodina.myrecipes.data.entity.CategoryType
import com.vsmorodina.myrecipes.di.ApplicationComponent
import com.vsmorodina.myrecipes.di.DaggerApplicationComponent
import com.vsmorodina.myrecipes.di.module.AppModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipesApplication: Application() {
    lateinit var applicationComponent: ApplicationComponent

    companion object {
        private const val PREFS_NAME = "RecipesApplicationPreferences"
        private const val IS_FIRST_RUN = "isFirstRun"
    }

    override fun onCreate() {
        applicationComponent = DaggerApplicationComponent.builder().appModule(AppModule(this)).build()
        super.onCreate()
        val settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (settings.getBoolean(IS_FIRST_RUN, true)) {
            settings.edit().putBoolean(IS_FIRST_RUN, false).apply()
            val appDatabase = AppDatabase.getInstance(this)
            val categoryDao = appDatabase.categoryDao
            val categories = listOf(
                CategoryEntity(name = "Супы", photoUri = "", isDefault = true, type = CategoryType.SOUPS),
                CategoryEntity(name = "Салаты", photoUri = "", isDefault = true, type = CategoryType.SALADS),
                CategoryEntity(name = "Выпечка", photoUri = "", isDefault = true, type = CategoryType.BAKING),
                CategoryEntity(name = "Закуски", photoUri = "", isDefault = true, type = CategoryType.APPETIZERS),
                CategoryEntity(name = "Мясо", photoUri = "", isDefault = true, type = CategoryType.MEAT),
                CategoryEntity(name = "Гарниры", photoUri = "", isDefault = true, type = CategoryType.GARNISH),
                CategoryEntity(name = "Соусы", photoUri = "", isDefault = true, type = CategoryType.SAUCES),
                CategoryEntity(name = "Напитки", photoUri = "", isDefault = true, type = CategoryType.BEVERAGES),
            )
            CoroutineScope(Dispatchers.IO).launch {
                categoryDao.insertAll(categories)
            }

        }
    }
}