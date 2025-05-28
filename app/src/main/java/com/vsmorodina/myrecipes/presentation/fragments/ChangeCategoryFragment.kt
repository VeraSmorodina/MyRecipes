package com.vsmorodina.myrecipes.presentation.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.vsmorodina.myrecipes.R
import com.vsmorodina.myrecipes.RecipesApplication
import com.vsmorodina.myrecipes.databinding.FragmentChangeCategoryBinding
import com.vsmorodina.myrecipes.di.AppViewModelFactory
import com.vsmorodina.myrecipes.presentation.viewModels.ChangeCategoryViewModel
import java.io.File
import javax.inject.Inject

class ChangeCategoryFragment : Fragment() {
    @Inject
    lateinit var appViewModelFactory: AppViewModelFactory

    private var _binding: FragmentChangeCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ChangeCategoryViewModel
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private val pickImageCode = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (!isGranted) {
                    Toast.makeText(requireContext(), "Разрешение не получено", Toast.LENGTH_LONG)
                        .show()
                    return@registerForActivityResult
                }
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                pickImageLauncher.launch(intent)
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangeCategoryBinding.inflate(inflater, container, false)
        val view = binding.root

        createViewModel()
        initSaveCategoryButton()
        initImageView()

        return view
    }

    private fun initImageView() {
        binding.imageView.setOnClickListener {
            when (Build.VERSION.SDK_INT) {
                in 1..32 -> {
                    val puckImageFromGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    startActivityForResult(puckImageFromGallery, pickImageCode)
                }

                else -> {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
        }
    }

    private fun initSaveCategoryButton() {
        binding.saveCategory.setOnClickListener {
            val name = binding.editText.text.toString()
            viewModel.createCategory(name)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel(view)

        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode != RESULT_OK) return@registerForActivityResult
                result.data?.data?.let(::setImageFromURI)
            }
    }

    private fun createViewModel() {
        val application = requireNotNull(this.activity).application as RecipesApplication
        application.applicationComponent.inject(this)
        viewModel =
            ViewModelProvider(this, appViewModelFactory).get(ChangeCategoryViewModel::class.java)
        val categoryId = ChangeCategoryFragmentArgs.fromBundle(requireArguments()).categoryId
        viewModel.init(categoryId)
        viewModel.getCategory()
    }

    private fun bindViewModel(view: View) {
        observeLiveData(viewModel.errorLiveData) {
            Snackbar.make(view, it, Snackbar.LENGTH_LONG).show()
        }
        observeLiveData(viewModel.imagePathLiveData) {
            binding.imageView.setImageURI(Uri.parse(it))
        }
        observeLiveData(viewModel.successSavingCategoryLiveData) {
            it?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            }
        }

        viewModel.getCategory()?.let {
            observeLiveData(it) {
                binding.editText.setText(it.name)
                if (it.isDefault) {
                    it.getDefaultCategoryImage()?.let {
                        binding.imageView.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                it
                            )
                        )
                    }
                } else if (it.photoUri == "") {
                    binding.imageView.setImageResource(R.drawable.def1)
                } else
                    binding.imageView.setImageURI(Uri.fromFile(File(it.photoUri)))
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || requestCode != pickImageCode) return
        data?.data?.let(::setImageFromURI)
    }

    private fun setImageFromURI(selectedImageURI: Uri) {
        saveImageToAppDirectory(selectedImageURI)?.let { viewModel.saveImagePath(it) }
        binding.imageView.setImageURI(selectedImageURI)
    }

    private fun saveImageToAppDirectory(uri: Uri): String? {
        val contentResolver = requireContext().contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val fileName = "image_${System.currentTimeMillis()}.jpg"
        val file = File(requireContext().filesDir, fileName)

        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        return file.absolutePath // Возвращает путь к сохраненному файлу
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}