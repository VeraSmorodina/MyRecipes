package com.vsmorodina.myrecipes.presentation.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vsmorodina.myrecipes.R
import com.vsmorodina.myrecipes.data.entity.RecipeEntity
import com.vsmorodina.myrecipes.databinding.RecipesItemBinding
import com.vsmorodina.myrecipes.domain.entity.Recipe
import java.io.File

class RecipeItemsAdapter(val clickListener: (recipeId: Long) -> Unit) :
    ListAdapter<Recipe, RecipeItemsAdapter.RecipeItemsViewHolder>(RecipeDiffItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : RecipeItemsViewHolder = RecipeItemsViewHolder.inflateFrom(parent)

    override fun onBindViewHolder(holder: RecipeItemsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    class RecipeItemsViewHolder(val binding: RecipesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): RecipeItemsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecipesItemBinding.inflate(layoutInflater, parent, false)
                return RecipeItemsViewHolder(binding)
            }
        }

        fun bind(item: Recipe, clickListener: (itemId: Long) -> Unit) {
            with(binding) {
                recipeTitle.text = item.name
                if (item.photoUri == "") {
                    imageView.setImageResource(R.drawable.def1)
                } else
                    imageView.setImageURI(Uri.fromFile(File(item.photoUri)))
                root.setOnClickListener { clickListener(item.id) }
            }
        }
    }
}