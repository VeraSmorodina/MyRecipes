package com.vsmorodina.myrecipes.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.vsmorodina.myrecipes.R
import com.vsmorodina.myrecipes.data.AppDatabase
import com.vsmorodina.myrecipes.databinding.FragmentRecipeBinding
import com.vsmorodina.myrecipes.presentation.viewModels.RecipeViewModel
import com.vsmorodina.myrecipes.presentation.viewModels.RecipeViewModelFactory

class RecipeFragment : Fragment() {

    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.lifecycleOwner = viewLifecycleOwner

        val recipeId = RecipeFragmentArgs.fromBundle(requireArguments()).idArg

        val application = requireNotNull(this.activity).application
        val dao = AppDatabase.getInstance(application).recipeDao
        val viewModelFactory = RecipeViewModelFactory(recipeId, dao)
        val viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(RecipeViewModel::class.java)
        binding.viewModel = viewModel
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.action_settings -> {
//                Toast.makeText(requireContext(), "Настройки выбраны", Toast.LENGTH_SHORT).show()
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}