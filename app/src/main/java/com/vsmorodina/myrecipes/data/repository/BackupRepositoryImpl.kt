package com.vsmorodina.myrecipes.data.repository

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.vsmorodina.myrecipes.data.AppDatabase
import com.vsmorodina.myrecipes.data.backup.BackupArchive
import com.vsmorodina.myrecipes.domain.entity.BackupSummary
import com.vsmorodina.myrecipes.domain.repository.BackupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

class BackupRepositoryImpl @Inject constructor(
    private val context: Context,
    private val appDatabase: AppDatabase
) : BackupRepository {
    private val categoryDao get() = appDatabase.categoryDao
    private val recipeDao get() = appDatabase.recipeDao

    // Фото категорий и рецептов хранятся в filesDir (см. saveImageToAppDirectory во фрагментах)
    private val photosDir get() = context.filesDir

    override suspend fun exportBackup(destination: Uri): BackupSummary {
        val (categories, recipes) = appDatabase.withTransaction {
            categoryDao.getAll() to recipeDao.getAll()
        }
        return withContext(Dispatchers.IO) {
            val output = context.contentResolver.openOutputStream(destination)
                ?: throw FileNotFoundException("Не удалось открыть $destination для записи")
            output.use { BackupArchive.write(it, categories, recipes) }
        }
    }

    override suspend fun importBackup(source: Uri): BackupSummary {
        val backup = withContext(Dispatchers.IO) {
            val input = context.contentResolver.openInputStream(source)
                ?: throw FileNotFoundException("Не удалось открыть $source для чтения")
            input.use { BackupArchive.read(it, photosDir) }
        }

        val previousPhotos = try {
            appDatabase.withTransaction {
                val photos = categoryDao.getAll().map { it.photoUri } +
                        recipeDao.getAll().map { it.photoUri }
                recipeDao.deleteAll()
                categoryDao.deleteAll()
                categoryDao.insertAll(backup.categories)
                recipeDao.insertAll(backup.recipes)
                photos
            }
        } catch (e: Throwable) {
            // Транзакция откатилась — скопированные из архива фото больше не нужны
            withContext(NonCancellable + Dispatchers.IO) { backup.photoFiles.forEach { it.delete() } }
            throw e
        }

        withContext(Dispatchers.IO) { deletePhotos(previousPhotos) }
        return backup.summary
    }

    // Удаляем только файлы из папки приложения, чтобы не задеть ничего постороннего
    private fun deletePhotos(paths: List<String>) {
        val dir = photosDir.canonicalFile
        paths.filter { it.isNotBlank() }
            .map { File(it).canonicalFile }
            .filter { it.parentFile == dir }
            .forEach { it.delete() }
    }
}
