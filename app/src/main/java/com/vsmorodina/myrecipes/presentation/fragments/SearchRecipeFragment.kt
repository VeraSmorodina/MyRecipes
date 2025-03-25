package com.vsmorodina.myrecipes.presentation.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vsmorodina.myrecipes.R
import com.vsmorodina.myrecipes.data.AppDatabase
import com.vsmorodina.myrecipes.databinding.FragmentCategoriesBinding
import com.vsmorodina.myrecipes.databinding.FragmentSearchRecipeBinding
import com.vsmorodina.myrecipes.presentation.adapters.CategoryItemsAdapter
import com.vsmorodina.myrecipes.presentation.adapters.SearchRecipeItemAdapter
import com.vsmorodina.myrecipes.presentation.viewModels.CategoriesViewModel
import com.vsmorodina.myrecipes.presentation.viewModels.CategoriesViewModelFactory
import com.vsmorodina.myrecipes.presentation.viewModels.SearchRecipeViewModel
import com.vsmorodina.myrecipes.presentation.viewModels.SearchRecipeViewModelFactory
import kotlinx.coroutines.launch

class SearchRecipeFragment : Fragment() {
    private var _binding: FragmentSearchRecipeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchRecipeBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.lifecycleOwner = viewLifecycleOwner


        val query = SearchRecipeFragmentArgs.fromBundle(requireArguments()).searchRecipeArg
        val application = requireNotNull(this.activity).application
        val dao = AppDatabase.getInstance(application)
        val recipeDao = AppDatabase.getInstance(application).recipeDao
        val viewModelFactory = SearchRecipeViewModelFactory(query, recipeDao)
        val viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(SearchRecipeViewModel::class.java)
        binding.viewModel = viewModel


        val adapter = SearchRecipeItemAdapter(
            clickListener = { recipeId ->
                val action =
                    SearchRecipeFragmentDirections.actionSearchRecipeFragmentToRecipeFragment(
                        recipeId
                    )
                findNavController().navigate(action)
            }
        )
        binding.recipeList.adapter = adapter


        observeLiveData(viewModel.recipesLiveData) {
            adapter.submitList(it)
        }

        return view
    }

}