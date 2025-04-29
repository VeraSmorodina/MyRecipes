package com.vsmorodina.myrecipes.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.vsmorodina.myrecipes.domain.entity.Recipe

@Entity(
    tableName = "recipes",
    foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["category_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "category_id")
    val categoryId: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "ingredients")
    val ingredients: String,

    @ColumnInfo(name = "cookingAlgorithm")
    val cookingAlgorithm: String,

    @ColumnInfo(name = "photo_url")
    val photoUri: String,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false
) {
//    fun getRecipeInfo() =
//        "Название рецепта: $name \n\nИнгредиенты: \n$ingredients \n\nАлгоритм приготовления: \n$cookingAlgorithm"


    fun toModel() = Recipe(
        id = id,
        categoryId = categoryId,
        name = name,
        ingredients = ingredients,
        cookingAlgorithm = cookingAlgorithm,
        photoUri = photoUri,
        isFavorite = isFavorite
    )
}
