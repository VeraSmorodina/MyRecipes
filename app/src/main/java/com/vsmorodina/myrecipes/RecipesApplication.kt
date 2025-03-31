package com.vsmorodina.myrecipes

import android.app.Application
import android.content.Context
import com.vsmorodina.myrecipes.data.AppDatabase
import com.vsmorodina.myrecipes.data.entity.CategoryEntity
import com.vsmorodina.myrecipes.data.entity.CategoryType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipesApplication: Application() {
    companion object {
        private const val PREFS_NAME = "RecipesApplicationPreferences"
        private const val IS_FIRST_RUN = "isFirstRun"
    }

    override fun onCreate() {
        super.onCreate()

        val settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (settings.getBoolean(IS_FIRST_RUN, true)) {
            settings.edit().putBoolean(IS_FIRST_RUN, false).apply()
            val appDatabase = AppDatabase.getInstance(this)
            val categoryDao = appDatabase.categoryDao
            val categories = listOf(
                CategoryEntity(name = "Супы", photoUri = "", isDefault = true, type = CategoryType.SOUPS),
                CategoryEntity(name = "Салаты", photoUri = "", isDefault = true, type = CategoryType.SALADS),
            )
            CoroutineScope(Dispatchers.IO).launch {
                categoryDao.insertAll(categories)
            }

        }
    }
}