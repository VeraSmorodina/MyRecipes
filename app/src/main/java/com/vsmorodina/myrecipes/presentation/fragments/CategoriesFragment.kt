package com.vsmorodina.myrecipes.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import com.vsmorodina.myrecipes.R
import com.vsmorodina.myrecipes.databinding.FragmentCategoriesBinding
import com.vsmorodina.myrecipes.presentation.adapters.CategoryItemsAdapter

class CategoriesFragment : Fragment() {
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        val view = binding.root
//        val myButton: Button = view.findViewById(R.id.my_button)
//        myButton.setOnClickListener {
//            view.findNavController()
//                .navigate(R.id.action_categoriesFragment_to_recipesFragment)
//        }
        val adapter = CategoryItemsAdapter()
        binding.tasksList.adapter = adapter
        return view
    }
}