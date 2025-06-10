package com.vsmorodina.myrecipes.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vsmorodina.myrecipes.R
import com.vsmorodina.myrecipes.RecipesApplication
import com.vsmorodina.myrecipes.databinding.FragmentCategoriesBinding
import com.vsmorodina.myrecipes.di.AppViewModelFactory
import com.vsmorodina.myrecipes.presentation.adapters.CategoryItemsAdapter
import com.vsmorodina.myrecipes.presentation.viewModels.CategoriesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class CategoriesFragment : Fragment() {

    @Inject
    lateinit var appViewModelFactory: AppViewModelFactory
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CategoriesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.lifecycleOwner = viewLifecycleOwner

        val application = requireNotNull(this.activity).application as RecipesApplication
        application.applicationComponent.inject(this)

        viewModel =
            ViewModelProvider(this, appViewModelFactory).get(CategoriesViewModel::class.java)
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

        lifecycleScope.launch {
            viewModel.categoriesFlow.collectLatest {
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