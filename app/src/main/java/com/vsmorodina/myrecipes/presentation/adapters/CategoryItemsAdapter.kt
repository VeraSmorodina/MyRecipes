package com.vsmorodina.myrecipes.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.vsmorodina.myrecipes.R
import com.vsmorodina.myrecipes.data.entity.CategoryEntity

class CategoryItemsAdapter : RecyclerView.Adapter<CategoryItemsAdapter.CategoryItemsViewHolder>() {
    var data = listOf<CategoryEntity>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : CategoryItemsViewHolder = CategoryItemsViewHolder.inflateFrom(parent)

    override fun onBindViewHolder(holder: CategoryItemsViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    class CategoryItemsViewHolder(private val rootView: CardView)
        : RecyclerView.ViewHolder(rootView) {
        companion object {
            fun inflateFrom(parent: ViewGroup): CategoryItemsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.recipe_category_item, parent, false) as CardView
                return CategoryItemsViewHolder(view)
            }
        }

        fun bind(item: CategoryEntity) {
            rootView.findViewById<TextView>(R.id.category_title).text = item.name
        }
    }
}