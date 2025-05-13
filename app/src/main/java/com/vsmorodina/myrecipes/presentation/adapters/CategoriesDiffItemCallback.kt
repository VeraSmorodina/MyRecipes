package com.vsmorodina.myrecipes.presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import com.vsmorodina.myrecipes.data.entity.CategoryEntity
import com.vsmorodina.myrecipes.domain.entity.Category

class CategoriesDiffItemCallback: DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category) = (oldItem.id == newItem.id)
    override fun areContentsTheSame(oldItem: Category, newItem: Category) = (oldItem == newItem)
}