package com.vsmorodina.myrecipes.domain.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.vsmorodina.myrecipes.data.entity.CategoryType

data class Category(
    val id: Long,
    val name: String,
    val photoUri: String,
    val isDefault: Boolean = false,
    val type: CategoryType = CategoryType.NONE
)