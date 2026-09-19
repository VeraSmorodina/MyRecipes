package com.vsmorodina.myrecipes.presentation.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsmorodina.myrecipes.domain.entity.BackupSummary
import com.vsmorodina.myrecipes.domain.entity.InvalidBackupException
import com.vsmorodina.myrecipes.domain.useCase.ExportBackupUseCase
import com.vsmorodina.myrecipes.domain.useCase.ImportBackupUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface BackupEvent {
    data class ExportCompleted(val summary: BackupSummary) : BackupEvent
    data class ImportCompleted(val summary: BackupSummary) : BackupEvent
    data class ImportRejected(val reason: InvalidBackupException.Reason) : BackupEvent
    data object ExportFailed : BackupEvent
    data object ImportFailed : BackupEvent
}

class SettingsViewModel @Inject constructor(
    private val exportBackupUseCase: ExportBackupUseCase,
    private val importBackupUseCase: ImportBackupUseCase
) : ViewModel() {
    private val _inProgressLiveData = MutableLiveData(false)
    val inProgressLiveData: LiveData<Boolean> = _inProgressLiveData

    // Событие держится, пока фрагмент его не покажет и не вызовет onEventHandled()
    private val _eventLiveData = MutableLiveData<BackupEvent?>()
    val eventLiveData: LiveData<BackupEvent?> = _eventLiveData

    fun exportBackup(destination: Uri) = runOperation(BackupEvent.ExportFailed) {
        BackupEvent.ExportCompleted(exportBackupUseCase.invoke(destination))
    }

    fun importBackup(source: Uri) = runOperation(BackupEvent.ImportFailed) {
        BackupEvent.ImportCompleted(importBackupUseCase.invoke(source))
    }

    fun onEventHandled() {
        _eventLiveData.value = null
    }

    private fun runOperation(failureEvent: BackupEvent, operation: suspend () -> BackupEvent) {
        if (_inProgressLiveData.value == true) return
        _inProgressLiveData.value = true
        viewModelScope.launch {
            val event = try {
                operation()
            } catch (e: CancellationException) {
                throw e
            } catch (e: InvalidBackupException) {
                Log.w(TAG, "Invalid backup", e)
                BackupEvent.ImportRejected(e.reason)
            } catch (e: Exception) {
                Log.e(TAG, "Backup operation failed", e)
                failureEvent
            } finally {
                _inProgressLiveData.value = false
            }
            _eventLiveData.value = event
        }
    }

    private companion object {
        const val TAG = "SettingsViewModel"
    }
}
