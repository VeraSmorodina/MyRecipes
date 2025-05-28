package com.vsmorodina.myrecipes.domain.entity

import com.vsmorodina.myrecipes.R
import com.vsmorodina.myrecipes.data.entity.CategoryType

data class Category(
    val id: Long,
    val name: String,
    val photoUri: String,
    val isDefault: Boolean = false,
    val type: CategoryType = CategoryType.NONE
) {
    fun getDefaultCategoryImage(): Int? {
        if (!isDefault) return null
        return when (type) {
            CategoryType.NONE -> null
            CategoryType.SOUPS -> R.drawable.s1200
            CategoryType.SALADS -> R.drawable.s1
            CategoryType.BAKING -> R.drawable.s2
            CategoryType.APPETIZERS -> R.drawable.s3
            CategoryType.MEAT -> R.drawable.s2007
            CategoryType.GARNISH -> R.drawable.s5
            CategoryType.BEVERAGES -> R.drawable.s6
            CategoryType.SAUCES -> R.drawable.s7
        }
    }
}