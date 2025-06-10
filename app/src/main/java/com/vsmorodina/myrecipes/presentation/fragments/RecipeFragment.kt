package com.vsmorodina.myrecipes.presentation.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.vsmorodina.myrecipes.R
import com.vsmorodina.myrecipes.RecipesApplication
import com.vsmorodina.myrecipes.databinding.FragmentRecipeBinding
import com.vsmorodina.myrecipes.di.AppViewModelFactory
import com.vsmorodina.myrecipes.domain.entity.Recipe
import com.vsmorodina.myrecipes.presentation.viewModels.RecipeViewModel
import java.io.File
import javax.inject.Inject

class RecipeFragment : Fragment() {

    @Inject
    lateinit var appViewModelFactory: AppViewModelFactory

    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as RecipesApplication).applicationComponent.inject(this)
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.lifecycleOwner = viewLifecycleOwner

        val recipeId = RecipeFragmentArgs.fromBundle(requireArguments()).idArg

        val application = requireNotNull(this.activity).application as RecipesApplication
        application.applicationComponent.inject(this)
        viewModel =
            ViewModelProvider(this, appViewModelFactory).get(RecipeViewModel::class.java)
        viewModel.init(recipeId)
        viewModel.getRecipe()
        binding.viewModel = viewModel
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData(viewModel.deleteRecipeCompletedLiveData) {
            findNavController().navigateUp()
        }
        observeLiveData(viewModel.recipeLiveData) {
            if (it.photoUri.isBlank())
                binding.imageView.setImageResource(R.drawable.image_def)
            else
                binding.imageView.setImageURI(Uri.fromFile(File(it.photoUri)))
            fillRecipeFields(it)
        }
        observeLiveData(viewModel.isFavoriteLiveData) {
            binding.favoriteButton.setImageResource(
                if (it) R.drawable.ic_favorite_red
                else R.drawable.ic_favorite
            )
        }
        viewModel.getFavoriteStatus()
        binding.favoriteButton.setOnClickListener {
            viewModel.updateFavorite()
        }
    }

    private fun fillRecipeFields(recipe: Recipe) {
        with(binding) {
            recipeNameTitle.text = recipe.name
            recipeIngredientsTitle.text = recipe.ingredients
            recipeCookingAlgorithmTitle.text = recipe.cookingAlgorithm
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> {
                viewModel.deleteRecipe()
                return true
            }

            R.id.action_change -> {
                val recipeId = RecipeFragmentArgs.fromBundle(requireArguments()).idArg
                val action =
                    RecipeFragmentDirections.actionRecipeFragmentToChangeRecipeFragment(recipeId)
                findNavController().navigate(action)
                return true
            }

            R.id.action_share -> {
                val text = viewModel.getRecipeInfo()
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, text)
                val chooserIntent = Intent.createChooser(intent, "Поделиться рецептом")
                requireContext().startActivity(chooserIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}