package com.vsmorodina.myrecipes.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.vsmorodina.myrecipes.data.AppDatabase
import com.vsmorodina.myrecipes.databinding.FragmentFavouritesRecipesBinding
import com.vsmorodina.myrecipes.presentation.adapters.FavouritesRecipeItemAdapter
import com.vsmorodina.myrecipes.presentation.viewModels.FavouritesRecipesViewModel
import com.vsmorodina.myrecipes.presentation.viewModels.FavouritesRecipesViewModelFactory

class FavouritesRecipesFragment : Fragment() {

    private var _binding: FragmentFavouritesRecipesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFavouritesRecipesBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = FavouritesRecipeItemAdapter { recipeId ->
            val action = FavouritesRecipesFragmentDirections.actionFavoritesFragmentToRecipeFragment(recipeId)
            findNavController().navigate(action)
        }
        binding.recipeList.adapter = adapter

        val application = requireNotNull(this.activity).application
        val dao = AppDatabase.getInstance(application).recipeDao
        val viewModelFactory = FavouritesRecipesViewModelFactory(dao)
        val viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(FavouritesRecipesViewModel::class.java)
        binding.viewModel = viewModel


        viewModel.recipesLiveData.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
            }
        }

        return view
    }
}