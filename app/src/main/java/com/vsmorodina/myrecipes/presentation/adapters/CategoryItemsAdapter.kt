package com.vsmorodina.myrecipes.presentation.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vsmorodina.myrecipes.R
import com.vsmorodina.myrecipes.data.entity.CategoryEntity
import com.vsmorodina.myrecipes.data.entity.CategoryType
import com.vsmorodina.myrecipes.databinding.RecipeCategoryItemBinding
import java.io.File

class CategoryItemsAdapter(
    val onСlickCategory: (categoryId: Long) -> Unit,
    val onDeleteCategory: (categoryId: Long) -> Unit,
    val onChangeCategory: (categoryId: Long) -> Unit
) :
    ListAdapter<CategoryEntity, CategoryItemsAdapter.CategoryItemsViewHolder>(
        CategoriesDiffItemCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : CategoryItemsViewHolder = CategoryItemsViewHolder.inflateFrom(parent)

    override fun onBindViewHolder(holder: CategoryItemsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onСlickCategory, onDeleteCategory, onChangeCategory)
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

        fun bind(
            item: CategoryEntity,
            clickListener: (itemId: Long) -> Unit,
            onDeleteCategory: (categoryId: Long) -> Unit,
            onChangeCategory: (categoryId: Long) -> Unit
        ) {
            with(binding) {
                categoryTitle.text = item.name
                if (item.isDefault) {
                    when (item.type) {
                        CategoryType.NONE -> {}
                        CategoryType.SOUPS -> imageView.setImageDrawable(
                            ContextCompat.getDrawable(
                                root.context,
                                R.drawable.s1200
                            )
                        )

                        CategoryType.SALADS -> imageView.setImageDrawable(
                            ContextCompat.getDrawable(
                                root.context,
                                R.drawable.s1
                            )
                        )

                        CategoryType.BAKING -> imageView.setImageDrawable(
                            ContextCompat.getDrawable(
                                root.context,
                                R.drawable.s2
                            )
                        )
                        CategoryType.APPETIZERS -> imageView.setImageDrawable(
                            ContextCompat.getDrawable(
                                root.context,
                                R.drawable.s3
                            )
                        )
                        CategoryType.MEAT -> imageView.setImageDrawable(
                            ContextCompat.getDrawable(
                                root.context,
                                R.drawable.s2007
                            )
                        )
                        CategoryType.GARNISH -> imageView.setImageDrawable(
                            ContextCompat.getDrawable(
                                root.context,
                                R.drawable.s5
                            )
                        )
                        CategoryType.BEVERAGES -> imageView.setImageDrawable(
                            ContextCompat.getDrawable(
                                root.context,
                                R.drawable.s6
                            )
                        )
                        CategoryType.SAUCES -> imageView.setImageDrawable(
                            ContextCompat.getDrawable(
                                root.context,
                                R.drawable.s7
                            )
                        )
                    }
                }else if (item.photoUri == ""){
                    imageView.setImageResource(R.drawable.def1)
                } else
                    imageView.setImageURI(Uri.fromFile(File(item.photoUri)))
                root.setOnClickListener { clickListener(item.id) }
                menuDots.setOnClickListener {
                    showPopup(
                        menuDots,
                        { onDeleteCategory(item.id) },
                        { onChangeCategory(item.id) })
                }

            }

        }

        private fun showPopup(
            anchorView: View,
            onDeleteCategory: () -> Unit,
            onChangeCategory: () -> Unit
        ) {
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
            editButton.setOnClickListener {
                onChangeCategory()
                popupWindow.dismiss()
            }

            val deleteButton = popupView.findViewById<TextView>(R.id.delete_button)
            deleteButton.setOnClickListener {
                onDeleteCategory()
                popupWindow.dismiss()
            }

            popupWindow.showAsDropDown(anchorView)
        }
    }
}