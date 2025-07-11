package com.vsmorodina.myrecipes.presentation.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vsmorodina.myrecipes.databinding.RecipesItemBinding
import com.vsmorodina.myrecipes.domain.entity.Recipe
import java.io.File

class SearchRecipeItemAdapter(private val clickListener: (recipeId: Long) -> Unit) :
    ListAdapter<Recipe, SearchRecipeItemAdapter.SearchRecipeItemsViewHolder>(
        RecipeDiffItemCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : SearchRecipeItemsViewHolder = SearchRecipeItemsViewHolder.inflateFrom(parent)

    override fun onBindViewHolder(holder: SearchRecipeItemsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    class SearchRecipeItemsViewHolder(val binding: RecipesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): SearchRecipeItemsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecipesItemBinding.inflate(layoutInflater, parent, false)
                return SearchRecipeItemsViewHolder(binding)
            }
        }

        fun bind(item: Recipe, clickListener: (itemId: Long) -> Unit) {
            with(binding) {
                recipeTitle.text = item.name
                imageView.setImageURI(Uri.fromFile(File(item.photoUri)))
                root.setOnClickListener { clickListener(item.id) }
            }
        }
    }
}