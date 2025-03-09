package com.vsmorodina.myrecipes.presentation.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vsmorodina.myrecipes.data.entity.CategoryEntity
import com.vsmorodina.myrecipes.databinding.RecipeCategoryItemBinding
import java.io.File

class CategoryItemsAdapter(val clickListener: (categoryId: Long) -> Unit) :
    ListAdapter<CategoryEntity, CategoryItemsAdapter.CategoryItemsViewHolder>(
        CategoriesDiffItemCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : CategoryItemsViewHolder = CategoryItemsViewHolder.inflateFrom(parent)

    override fun onBindViewHolder(holder: CategoryItemsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    class CategoryItemsViewHolder(val binding: RecipeCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): CategoryItemsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecipeCategoryItemBinding.inflate(layoutInflater, parent, false)
                return CategoryItemsViewHolder(binding)
            }
        }

        fun bind(item: CategoryEntity, clickListener: (itemId: Long) -> Unit) {
            with(binding) {
                categoryTitle.text = item.name
                imageView.setImageURI(Uri.fromFile(File(item.photoUri)))
                root.setOnClickListener { clickListener(item.id) }
            }

        }
    }
}