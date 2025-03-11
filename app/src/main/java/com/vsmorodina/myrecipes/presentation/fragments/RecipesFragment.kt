package com.vsmorodina.myrecipes.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.vsmorodina.myrecipes.data.AppDatabase
import com.vsmorodina.myrecipes.databinding.FragmentRecipesBinding
import com.vsmorodina.myrecipes.presentation.adapters.RecipeItemsAdapter
import com.vsmorodina.myrecipes.presentation.viewModels.RecipesViewModel
import com.vsmorodina.myrecipes.presentation.viewModels.RecipesViewModelFactory

class RecipesFragment : Fragment() {
    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.lifecycleOwner = viewLifecycleOwner
//        val adapter = RecipeItemsAdapter{view.findNavController()
//            .navigate(R.id.action_recipesFragment_to_recipeFragment)}

        val categoryId = RecipesFragmentArgs.fromBundle(requireArguments()).categoryId

        val application = requireNotNull(this.activity).application
        val dao = AppDatabase.getInstance(application).recipeDao
        val viewModelFactory = RecipesViewModelFactory(categoryId, dao)
        val viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(RecipesViewModel::class.java)
        binding.viewModel = viewModel

        val adapter = RecipeItemsAdapter { recipeId -> viewModel.onRecipeClicked(recipeId) }
        binding.recipeList.adapter = adapter

        viewModel.recipesLiveData.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
            }
        }

        viewModel.navigateToRecipe.observe(viewLifecycleOwner) { recipeId ->
            recipeId?.let {
                val action = RecipesFragmentDirections
                    .actionRecipesFragmentToRecipeFragment(recipeId)
                this.findNavController().navigate(action)
                viewModel.onRecipeNavigated()
            }
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}