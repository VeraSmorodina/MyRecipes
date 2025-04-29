package com.vsmorodina.myrecipes.presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import com.vsmorodina.myrecipes.data.entity.RecipeEntity
import com.vsmorodina.myrecipes.domain.entity.Recipe

class RecipeDiffItemCallback: DiffUtil.ItemCallback<Recipe>() {
    override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe) = (oldItem.id == newItem.id)
    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe) = (oldItem == newItem)
}