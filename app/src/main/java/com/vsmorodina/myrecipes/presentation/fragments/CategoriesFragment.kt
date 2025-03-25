package com.vsmorodina.myrecipes.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.lifecycleOwner = viewLifecycleOwner

        val application = requireNotNull(this.activity).application
        val dao = AppDatabase.getInstance(application)
        val categoryDao = AppDatabase.getInstance(application).categoryDao
        val viewModelFactory = CategoriesViewModelFactory(categoryDao)
        val viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(CategoriesViewModel::class.java)
        binding.viewModel = viewModel

        val adapter = CategoryItemsAdapter(
            onСlickCategory = { categoryId ->
                val action = CategoriesFragmentDirections.actionCategoriesFragmentToRecipesFragment(
                    categoryId
                )
                findNavController().navigate(action)
            },
            onDeleteCategory = { categoryId ->
                viewModel.deleteCategory(categoryId)
            },
            onChangeCategory = { categoryId ->
                val action =
                    CategoriesFragmentDirections.actionCategoriesFragmentToChangeCategoryFragment(
                        categoryId
                    )
                findNavController().navigate(action)
            }
        )
        binding.tasksList.adapter = adapter


        viewModel.categoriesLiveData.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
            }
        }

        binding.myButton.setOnClickListener {
            findNavController().navigate(R.id.action_categoriesFragment_to_createCategoryFragment2)
        }


        with(binding) {
            searchEditText.imeOptions = EditorInfo.IME_ACTION_DONE
            searchEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val action =
                        CategoriesFragmentDirections.actionCategoriesFragmentToSearchRecipeFragment(
                            searchEditText.text.toString()
                        )
                    findNavController().navigate(action)
                }
                true
            }

        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}