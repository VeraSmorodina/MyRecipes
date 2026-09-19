package com.vsmorodina.myrecipes.domain.useCase

import android.net.Uri
import com.vsmorodina.myrecipes.domain.repository.BackupRepository
import javax.inject.Inject

class ImportBackupUseCase @Inject constructor(private val backupRepository: BackupRepository) {
    suspend fun invoke(source: Uri) = backupRepository.importBackup(source)
}
