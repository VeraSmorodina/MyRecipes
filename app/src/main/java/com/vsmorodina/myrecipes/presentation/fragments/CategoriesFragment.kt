package com.vsmorodina.myrecipes.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.vsmorodina.myrecipes.R
import com.vsmorodina.myrecipes.data.AppDatabase
import com.vsmorodina.myrecipes.databinding.FragmentCategoriesBinding
import com.vsmorodina.myrecipes.presentation.adapters.CategoryItemsAdapter
import com.vsmorodina.myrecipes.presentation.viewModels.CategoriesViewModelFactory
import com.vsmorodina.myrecipes.presentation.viewModels.CategoriesViewModel

class CategoriesFragment : Fragment() {
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.lifecycleOwner = viewLifecycleOwner
        val adapter = CategoryItemsAdapter{view.findNavController()
                .navigate(R.id.action_categoriesFragment_to_recipesFragment)}
        binding.tasksList.adapter = adapter

        val application = requireNotNull(this.activity).application
        val dao = AppDatabase.getInstance(application).categoryDao
        val viewModelFactory = CategoriesViewModelFactory(dao)
        val viewModel = ViewModelProvider(
            this, viewModelFactory).get(CategoriesViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.categoriesLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}