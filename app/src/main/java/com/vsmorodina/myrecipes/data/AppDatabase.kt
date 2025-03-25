package com.vsmorodina.myrecipes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vsmorodina.myrecipes.data.dao.CategoryDao
import com.vsmorodina.myrecipes.data.dao.RecipeDao
import com.vsmorodina.myrecipes.data.dao.RecipePhotoDao
import com.vsmorodina.myrecipes.data.entity.CategoryEntity
import com.vsmorodina.myrecipes.data.entity.RecipeEntity
import com.vsmorodina.myrecipes.data.entity.RecipePhotoEntity

@Database(
    entities = [CategoryEntity::class, RecipeEntity::class, RecipePhotoEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val categoryDao: CategoryDao
    abstract val recipeDao: RecipeDao
    abstract val recipePhotoDao: RecipePhotoDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app_database"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}