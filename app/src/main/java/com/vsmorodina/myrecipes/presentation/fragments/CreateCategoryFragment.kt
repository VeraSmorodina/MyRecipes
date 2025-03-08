package com.vsmorodina.myrecipes.presentation.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.vsmorodina.myrecipes.data.AppDatabase
import com.vsmorodina.myrecipes.databinding.FragmentCreateCategoryBinding
import com.vsmorodina.myrecipes.presentation.viewModels.CreateCategoryViewModel
import com.vsmorodina.myrecipes.presentation.viewModels.CreateCategoryViewModelFactory
import java.io.IOException

class CreateCategoryFragment : Fragment() {
    private var _binding: FragmentCreateCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CreateCategoryViewModel
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateCategoryBinding.inflate(inflater, container, false)
        val view = binding.root

        val application = requireNotNull(this.activity).application
        val categoryDao = AppDatabase.getInstance(application).categoryDao
        val viewModelFactory = CreateCategoryViewModelFactory(categoryDao)
        viewModel = ViewModelProvider(
            this, viewModelFactory
        )[CreateCategoryViewModel::class.java]

        binding.saveCategory.setOnClickListener {
            val name = binding.editText.text.toString()
            viewModel.createCategory(name)
        }

        binding.imageView.setOnClickListener {
            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            Snackbar.make(view, it, Snackbar.LENGTH_LONG).show()
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    pickImageLauncher.launch(intent)
                    Log.d("Permission", "Разрешение получено")
                } else {
                    Log.d("Permission", "Разрешение не получено")
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val selectedImage = result.data?.data ?: return@registerForActivityResult
                    try {
                        val image = MediaStore.Images.Media.getBitmap(
                            requireContext().contentResolver,
                            selectedImage
                        )
                        binding.imageView.setImageBitmap(image)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            requireContext(),
                            "Возникла ошибка при получении фото",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}