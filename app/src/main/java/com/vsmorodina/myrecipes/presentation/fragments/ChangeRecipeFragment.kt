package com.vsmorodina.myrecipes.presentation.fragments

import android.Manifest
import android.R
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.vsmorodina.myrecipes.data.AppDatabase
import com.vsmorodina.myrecipes.databinding.FragmentChangeRecipeBinding
import com.vsmorodina.myrecipes.presentation.viewModels.ChangeRecipeViewModel
import com.vsmorodina.myrecipes.presentation.viewModels.ChangeRecipeViewModelFactory
import java.io.File

class ChangeRecipeFragment : Fragment() {

    private var _binding: FragmentChangeRecipeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ChangeRecipeViewModel

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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeRecipeBinding.inflate(inflater, container, false)
        val view = binding.root

        createViewModel()
        initSaveRecipeBtn()
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

    private fun initSaveRecipeBtn() {
        binding.saveRecipeBtn.setOnClickListener {
            val selectedCategoryIndex = binding.categorySpinner.selectedItemPosition
            val name = binding.recipeNameEditText.text.toString()
            val ing = binding.ingrNameEditText.text.toString()
            val alg = binding.algNameEditText.text.toString()
            viewModel.changeRecipe(
                selectedCategoryIndex = selectedCategoryIndex,
                name = name,
                ingredients = ing,
                cookingAlgorithm = alg
            )
        }
    }

    private fun createViewModel() {
        val recipeId = ChangeRecipeFragmentArgs.fromBundle(requireArguments()).idRecipeChange


        binding.lifecycleOwner = viewLifecycleOwner
        val application = requireNotNull(this.activity).application
        val recipeDao = AppDatabase.getInstance(application).recipeDao
        val categoryDao = AppDatabase.getInstance(application).categoryDao
        val viewModelFactory = ChangeRecipeViewModelFactory(recipeId, recipeDao, categoryDao)
        viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(ChangeRecipeViewModel::class.java)
        binding.viewModel = viewModel
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_dropdown_item,
            mutableListOf<String>()
        )
        binding.categorySpinner.adapter = adapter
        viewModel.categories.observe(viewLifecycleOwner) { newData ->
            adapter.clear()
            adapter.addAll(newData.map {
                it.name
            })
            adapter.notifyDataSetChanged()
        }

/////////////////////////

        bindViewModel(view)

        viewModel.calculateCategoryIndex()


        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode != RESULT_OK) return@registerForActivityResult
                result.data?.data?.let(::setImageFromURI)
            }
    }

    private fun bindViewModel(view: View) {
        observeLiveData(viewModel.errorLiveData) {
            Snackbar.make(view, it, Snackbar.LENGTH_LONG).show()
        }
        observeLiveData(viewModel.successSavingRecipeLiveData) {
            it?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            }
        }
        observeLiveData(viewModel.imagePathLiveData) {
            binding.imageView.setImageURI(Uri.parse(it))
        }
        observeLiveData(viewModel.selectedCategoryIndexLiveData) {
            binding.categorySpinner.setSelection(it)
        }
        observeLiveData(viewModel.recipeLiveData){
            binding.imageView.setImageURI(Uri.parse(it.photoUri))
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