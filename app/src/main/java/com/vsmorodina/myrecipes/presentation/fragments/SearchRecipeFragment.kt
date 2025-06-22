package com.vsmorodina.myrecipes.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.vsmorodina.myrecipes.RecipesApplication
import com.vsmorodina.myrecipes.databinding.FragmentSearchRecipeBinding
import com.vsmorodina.myrecipes.di.AppViewModelFactory
import com.vsmorodina.myrecipes.domain.entity.Recipe
import com.vsmorodina.myrecipes.presentation.adapters.SearchRecipeItemAdapter
import com.vsmorodina.myrecipes.presentation.viewModels.SearchRecipeViewModel
import javax.inject.Inject

class SearchRecipeFragment : Fragment() {
    @Inject
    lateinit var appViewModelFactory: AppViewModelFactory
    private var _binding: FragmentSearchRecipeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SearchRecipeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchRecipeBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.lifecycleOwner = viewLifecycleOwner

        val query = SearchRecipeFragmentArgs.fromBundle(requireArguments()).searchRecipeArg

        val application = requireNotNull(this.activity).application as RecipesApplication
        application.applicationComponent.inject(this)

        viewModel =
            ViewModelProvider(this, appViewModelFactory).get(SearchRecipeViewModel::class.java)

        viewModel.init(query)
        viewModel.getRecipes()
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

        viewModel.getRecipes()?.let {
            observeLiveData(it) {
                adapter.submitList(it.map {
                    Recipe(
                        id = it.id,
                        categoryId = it.categoryId,
                        name = it.name,
                        ingredients = it.ingredients,
                        cookingAlgorithm = it.cookingAlgorithm,
                        photoUri = it.photoUri,
                        isFavorite = it.isFavorite
                    )
                })
            }
        }
        return view
    }
}