package com.vsmorodina.myrecipes.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import com.vsmorodina.myrecipes.R

class CategoriesFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_categories, container, false)
        val myButton: Button = view.findViewById(R.id.my_button)
        myButton.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_categoriesFragment_to_recipesFragment)
        }

        return view
    }
}