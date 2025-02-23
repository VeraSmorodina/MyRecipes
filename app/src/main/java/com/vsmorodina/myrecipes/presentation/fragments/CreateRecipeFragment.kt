package com.vsmorodina.myrecipes.presentation.fragments

import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.vsmorodina.myrecipes.data.AppDatabase
import com.vsmorodina.myrecipes.databinding.FragmentCreateRecipeBinding
import com.vsmorodina.myrecipes.presentation.viewModels.CreateRecipeViewModel
import com.vsmorodina.myrecipes.presentation.viewModels.CreateRecipeViewModelFactory

class CreateRecipeFragment : Fragment() {

    private var _binding: FragmentCreateRecipeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CreateRecipeViewModel


    private lateinit var imageView: ImageView
    private val PICK_IMAGE_REQUEST = 1


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateRecipeBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.lifecycleOwner = viewLifecycleOwner
        val application = requireNotNull(this.activity).application
        val categoryDao = AppDatabase.getInstance(application).categoryDao
        val recipeDao = AppDatabase.getInstance(application).recipeDao
        val viewModelFactory = CreateRecipeViewModelFactory(categoryDao, recipeDao)
        viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(CreateRecipeViewModel::class.java)
        binding.viewModel = viewModel
        binding.saveRecipeBtn.setOnClickListener {
            val selectedCategoryIndex = binding.categorySpinner.selectedItemPosition
            val name = binding.recipeNameEditText.text.toString()
            val ing = binding.ingrNameEditText.text.toString()
            val alg = binding.algNameEditText.text.toString()
            viewModel.createRecipe(
                selectedCategoryIndex = selectedCategoryIndex,
                name = name,
                ingredients = ing,
                cookingAlgorithm = alg
            )
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            Snackbar.make(view, it, Snackbar.LENGTH_LONG).show()
        }

        return view
    }


//    fun openGallery() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        startActivityForResult(intent, PICK_IMAGE_REQUEST)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
//            selectedImageUri = data.data
//            binding.invalidateAll() // Refresh binding to update ImageView
//        }
//    }

    // адаптер для спинера
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}