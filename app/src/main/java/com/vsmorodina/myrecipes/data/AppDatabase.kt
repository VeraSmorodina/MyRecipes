package com.vsmorodina.myrecipes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vsmorodina.myrecipes.data.dao.CategoryDao
import com.vsmorodina.myrecipes.data.dao.RecipeDao
import com.vsmorodina.myrecipes.data.dao.RecipePhotoDao
import com.vsmorodina.myrecipes.data.entity.CategoryEntity
import com.vsmorodina.myrecipes.data.entity.RecipeEntity
import com.vsmorodina.myrecipes.data.entity.RecipePhotoEntity

@Database(
    entities = [CategoryEntity::class, RecipeEntity::class, RecipePhotoEntity::class],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract val categoryDao: CategoryDao
    abstract val recipeDao: RecipeDao
    abstract val recipePhotoDao: RecipePhotoDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Под номером 2 выходили три разные схемы, версия при этом не повышалась:
         * - 1.2–1.3: в categories нет колонки type;
         * - 1.4–1.6: нет индекса index_recipes_category_id;
         * - 1.7–1.8: текущая схема.
         * Миграция приводит любую из них к текущей схеме, не трогая данные.
         */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                if (!db.hasColumn("categories", "type")) {
                    db.execSQL("ALTER TABLE `categories` ADD COLUMN `type` TEXT NOT NULL DEFAULT 'NONE'")
                }
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_recipes_category_id` ON `recipes` (`category_id`)")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app_database"
                    )
                        .addMigrations(MIGRATION_2_3)
                        // База версии 1 была только в ранних сборках до 1.2. Для остальных версий
                        // нужна миграция: без неё приложение упадёт, а не удалит данные пользователя молча
                        .fallbackToDestructiveMigrationFrom(1)
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

        private fun SupportSQLiteDatabase.hasColumn(table: String, column: String): Boolean =
            query("PRAGMA table_info(`$table`)").use { cursor ->
                val nameIndex = cursor.getColumnIndexOrThrow("name")
                while (cursor.moveToNext()) {
                    if (cursor.getString(nameIndex) == column) return true
                }
                false
            }
    }
}
