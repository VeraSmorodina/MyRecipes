package com.vsmorodina.myrecipes.presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import com.vsmorodina.myrecipes.data.entity.RecipeEntity

class SearchRecipeItemCallback: DiffUtil.ItemCallback<RecipeEntity>() {
    override fun areItemsTheSame(oldItem: RecipeEntity, newItem: RecipeEntity) = (oldItem.id == newItem.id)
    override fun areContentsTheSame(oldItem: RecipeEntity, newItem: RecipeEntity) = (oldItem == newItem)
}