package com.vsmorodina.myrecipes.domain.entity

// Итог экспорта или импорта резервной копии
data class BackupSummary(
    val categoriesCount: Int,
    val recipesCount: Int,
    val photosCount: Int
)

// Файл нельзя импортировать: это не резервная копия, она повреждена или создана более новой версией
class InvalidBackupException(val reason: Reason, cause: Throwable? = null) :
    Exception("Некорректная резервная копия: $reason", cause) {

    enum class Reason {
        NOT_A_BACKUP,
        UNSUPPORTED_VERSION,
        CORRUPTED
    }
}
