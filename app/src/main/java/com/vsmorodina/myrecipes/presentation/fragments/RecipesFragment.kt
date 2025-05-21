package com.vsmorodina.myrecipes.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vsmorodina.myrecipes.RecipesApplication
import com.vsmorodina.myrecipes.databinding.FragmentRecipesBinding
import com.vsmorodina.myrecipes.di.AppViewModelFactory
import com.vsmorodina.myrecipes.domain.useCase.GetRecipesUseCase
import com.vsmorodina.myrecipes.presentation.adapters.RecipeItemsAdapter
import com.vsmorodina.myrecipes.presentation.viewModels.RecipesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class RecipesFragment : Fragment() {
    @Inject
    lateinit var appViewModelFactory: AppViewModelFactory

    @Inject
    lateinit var getRecipesUseCase: GetRecipesUseCase
    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RecipesViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.lifecycleOwner = viewLifecycleOwner

        val categoryId = RecipesFragmentArgs.fromBundle(requireArguments()).categoryId

        val application = requireNotNull(this.activity).application as RecipesApplication
        application.applicationComponent.inject(this)

        viewModel =
            ViewModelProvider(this, appViewModelFactory).get(RecipesViewModel::class.java)
        binding.viewModel = viewModel
        viewModel.getRecipes(categoryId)

//        val viewModelFactory = RecipesViewModelFactory(categoryId, getRecipesUseCase)
//        val viewModel = ViewModelProvider(
//            this, viewModelFactory
//        ).get(RecipesViewModel::class.java)
//        binding.viewModel = viewModel

        val adapter = RecipeItemsAdapter { recipeId ->
            val action = RecipesFragmentDirections
                .actionRecipesFragmentToRecipeFragment(recipeId)
            findNavController().navigate(action)
        }
        binding.recipeList.adapter = adapter

        lifecycleScope.launch {
            viewModel.recipesFlow.collectLatest {
                adapter.submitList(it)
            }
        }
//        viewModel.recipesLiveData.observe(viewLifecycleOwner) {
//            it?.let(adapter::submitList)
//        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}