package com.vsmorodina.myrecipes.presentation.fragments

import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.vsmorodina.myrecipes.data.AppDatabase
import com.vsmorodina.myrecipes.databinding.FragmentCreateRecipeBinding
import com.vsmorodina.myrecipes.presentation.viewModels.CreateRecipeViewModel
import com.vsmorodina.myrecipes.presentation.viewModels.CreateRecipeViewModelFactory

class CreateRecipeFragment : Fragment() {

    private var _binding: FragmentCreateRecipeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CreateRecipeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateRecipeBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.lifecycleOwner = viewLifecycleOwner


        val application = requireNotNull(this.activity).application
        val dao = AppDatabase.getInstance(application).categoryDao
        val viewModelFactory = CreateRecipeViewModelFactory(dao)
        viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(CreateRecipeViewModel::class.java)
        binding.viewModel = viewModel


        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ArrayAdapter(
            requireContext(),
//            R.layout.simple_spinner_item,
            R.layout.simple_spinner_dropdown_item,
            viewModel.categories.value?.toMutableList()?.map {
                it.name
            } ?: mutableListOf()
        )

//        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.yourSpinnerId.adapter = adapter

        viewModel.categories.observe(viewLifecycleOwner) { newData ->
            adapter.clear()
            adapter.addAll(newData.map {
                it.name
            })
            adapter.notifyDataSetChanged()
        }
    }
}