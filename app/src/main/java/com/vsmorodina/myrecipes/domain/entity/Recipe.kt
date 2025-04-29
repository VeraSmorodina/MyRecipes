package com.vsmorodina.myrecipes.domain.entity

data class Recipe(
    val id: Long,
    val categoryId: Long,
    val name: String,
    val ingredients: String,
    val cookingAlgorithm: String,
    val photoUri: String,
    val isFavorite: Boolean
) {
    fun getRecipeInfo() =
        "Название рецепта: $name \n\nИнгредиенты: \n$ingredients \n\nАлгоритм приготовления: \n$cookingAlgorithm"

}
