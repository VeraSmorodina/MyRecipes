package com.vsmorodina.myrecipes.presentation.adapters

import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vsmorodina.myrecipes.R
import com.vsmorodina.myrecipes.data.entity.RecipeEntity
import com.vsmorodina.myrecipes.databinding.RecipesItemBinding
import java.io.File


class FavouritesRecipeItemAdapter(val clickListener: (recipeId: Long) -> Unit) :
    ListAdapter<RecipeEntity, FavouritesRecipeItemAdapter.RecipeItemsViewHolder>(
        FavouritesRecipeDiffItemCallback()
    ) {

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

        fun bind(item: RecipeEntity, clickListener: (itemId: Long) -> Unit) {
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