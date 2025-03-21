package com.vsmorodina.myrecipes.presentation.adapters

import android.content.Context
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vsmorodina.myrecipes.R
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
                menuDots.setOnClickListener {
                    showPopup(menuDots)
                }
            }

        }

        private fun showPopup(anchorView: View) {
            // Загружаем разметку попапа
            val inflater = anchorView.context.getSystemService(LayoutInflater::class.java)
            val popupView: View = inflater.inflate(R.layout.category_actions_popup, null)

            val popupWindow = PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
            )

            val editButton = popupView.findViewById<TextView>(R.id.edit_button)
            editButton.setOnClickListener { popupWindow.dismiss() }

            val deleteButton = popupView.findViewById<TextView>(R.id.delete_button)
            deleteButton.setOnClickListener { popupWindow.dismiss() }

            popupWindow.showAsDropDown(anchorView)
        }
    }
}