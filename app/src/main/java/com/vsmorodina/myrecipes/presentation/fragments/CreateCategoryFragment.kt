package com.vsmorodina.myrecipes.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.vsmorodina.myrecipes.data.AppDatabase
import com.vsmorodina.myrecipes.databinding.FragmentCreateCategoryBinding
import com.vsmorodina.myrecipes.presentation.viewModels.CreateCategoryViewModel
import com.vsmorodina.myrecipes.presentation.viewModels.CreateCategoryViewModelFactory

class CreateCategoryFragment : Fragment() {
    private var _binding: FragmentCreateCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CreateCategoryViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCreateCategoryBinding.inflate(inflater, container, false)
        val view = binding.root

        val application = requireNotNull(this.activity).application
        val categoryDao = AppDatabase.getInstance(application).categoryDao
        val viewModelFactory = CreateCategoryViewModelFactory(categoryDao)
        viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(CreateCategoryViewModel::class.java)

        binding.saveCategory.setOnClickListener {
            val name = binding.editText.text.toString()
            viewModel.createCategory(name)
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            Snackbar.make(view, it, Snackbar.LENGTH_LONG).show()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}