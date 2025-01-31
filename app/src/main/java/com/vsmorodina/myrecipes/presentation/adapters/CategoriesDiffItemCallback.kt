package com.vsmorodina.myrecipes.presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import com.vsmorodina.myrecipes.data.entity.CategoryEntity

class CategoriesDiffItemCallback: DiffUtil.ItemCallback<CategoryEntity>() {
    override fun areItemsTheSame(oldItem: CategoryEntity, newItem: CategoryEntity) = (oldItem.id == newItem.id)
    override fun areContentsTheSame(oldItem: CategoryEntity, newItem: CategoryEntity) = (oldItem == newItem)
}