package com.vsmorodina.myrecipes.domain.repository

import android.net.Uri
import com.vsmorodina.myrecipes.domain.entity.BackupSummary

interface BackupRepository {
    suspend fun exportBackup(destination: Uri): BackupSummary

    // Заменяет все категории, рецепты и фото содержимым резервной копии
    suspend fun importBackup(source: Uri): BackupSummary
}
