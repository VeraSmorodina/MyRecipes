package com.vsmorodina.myrecipes.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.vsmorodina.myrecipes.R
import com.vsmorodina.myrecipes.RecipesApplication
import com.vsmorodina.myrecipes.databinding.FragmentSettingsBinding
import com.vsmorodina.myrecipes.di.AppViewModelFactory
import com.vsmorodina.myrecipes.domain.entity.InvalidBackupException
import com.vsmorodina.myrecipes.presentation.viewModels.BackupEvent
import com.vsmorodina.myrecipes.presentation.viewModels.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class SettingsFragment : Fragment() {
    @Inject
    lateinit var appViewModelFactory: AppViewModelFactory

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SettingsViewModel

    private val exportLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument(BACKUP_MIME_TYPE)) { uri ->
            uri?.let { viewModel.exportBackup(it) }
        }

    private val importLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let { viewModel.importBackup(it) }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.lifecycleOwner = viewLifecycleOwner

        val application = requireNotNull(this.activity).application as RecipesApplication
        application.applicationComponent.inject(this)

        viewModel =
            ViewModelProvider(this, appViewModelFactory).get(SettingsViewModel::class.java)
        binding.viewModel = viewModel

        binding.exportButton.setOnClickListener {
            exportLauncher.launch(backupFileName())
        }
        binding.importButton.setOnClickListener {
            confirmImport()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData(viewModel.inProgressLiveData) { inProgress ->
            binding.progressIndicator.isVisible = inProgress
            binding.exportButton.isEnabled = !inProgress
            binding.importButton.isEnabled = !inProgress
        }
        observeLiveData(viewModel.eventLiveData) { event ->
            event ?: return@observeLiveData
            Snackbar.make(view, messageFor(event), Snackbar.LENGTH_LONG).show()
            viewModel.onEventHandled()
        }
    }

    private fun confirmImport() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.settings_import_confirm_title)
            .setMessage(R.string.settings_import_confirm_message)
            .setPositiveButton(R.string.settings_import_confirm_positive) { _, _ ->
                importLauncher.launch(BACKUP_IMPORT_MIME_TYPES)
            }
            .setNegativeButton(R.string.settings_cancel, null)
            .show()
    }

    private fun backupFileName(): String {
        val date = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.US).format(Date())
        return getString(R.string.settings_backup_file_name, date)
    }

    private fun messageFor(event: BackupEvent): String = when (event) {
        is BackupEvent.ExportCompleted -> with(event.summary) {
            getString(R.string.settings_export_completed, categoriesCount, recipesCount, photosCount)
        }

        is BackupEvent.ImportCompleted -> with(event.summary) {
            getString(R.string.settings_import_completed, categoriesCount, recipesCount, photosCount)
        }

        is BackupEvent.ImportRejected -> getString(
            when (event.reason) {
                InvalidBackupException.Reason.NOT_A_BACKUP -> R.string.settings_import_not_a_backup
                InvalidBackupException.Reason.UNSUPPORTED_VERSION -> R.string.settings_import_unsupported_version
                InvalidBackupException.Reason.CORRUPTED -> R.string.settings_import_corrupted
            }
        )

        BackupEvent.ExportFailed -> getString(R.string.settings_export_failed)
        BackupEvent.ImportFailed -> getString(R.string.settings_import_failed)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val BACKUP_MIME_TYPE = "application/zip"

        // Некоторые файловые менеджеры отдают ZIP с нестандартным MIME-типом
        val BACKUP_IMPORT_MIME_TYPES =
            arrayOf("application/zip", "application/x-zip-compressed", "application/octet-stream")
    }
}
