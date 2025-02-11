package com.vsmorodina.myrecipes.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

//    @ColumnInfo(name = "category_id")
//    val categoryId: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "ingredients")
    val ingredients: String,

    @ColumnInfo(name = "cookingAlgorithm")
    val cookingAlgorithm: String,

    @ColumnInfo(name = "photo_url")
    val photoUrl: String,


//    @ColumnInfo(name = "is_favorites")
//    val isFavorites: Boolean
)
