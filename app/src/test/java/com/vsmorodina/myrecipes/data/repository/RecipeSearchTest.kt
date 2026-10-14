package com.vsmorodina.myrecipes.data.repository

import com.vsmorodina.myrecipes.data.entity.RecipeEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class RecipeSearchTest {
    private val recipes = listOf(
        recipe(1, "Борщ украинский"),
        recipe(2, "Гороховый суп"),
        recipe(3, "Суп-пюре из тыквы"),
        recipe(4, "Кислые щи"),
        recipe(5, "Pancakes")
    )

    @Test
    fun findsSubstringInsideWord() {
        assertEquals(listOf(2L), search("горох"))
        assertEquals(listOf(1L), search("краин"))
    }

    @Test
    fun ignoresCaseForCyrillicAndLatin() {
        assertEquals(listOf(1L), search("борщ"))
        assertEquals(listOf(4L), search("ЩИ"))
        assertEquals(listOf(2L, 3L), search("сУп"))
        assertEquals(listOf(5L), search("CAKE"))
    }

    @Test
    fun trimsSpacesAroundQuery() {
        assertEquals(listOf(1L), search("  борщ "))
    }

    @Test
    fun blankQueryFindsNothing() {
        assertEquals(emptyList<Long>(), search(""))
        assertEquals(emptyList<Long>(), search("   "))
    }

    @Test
    fun noMatchReturnsEmptyList() {
        assertEquals(emptyList<Long>(), search("плов"))
    }

    private fun search(query: String) =
        RecipesRepositoryImpl.filterByName(recipes, query).map { it.id }

    private fun recipe(id: Long, name: String) = RecipeEntity(
        id = id,
        categoryId = 1,
        name = name,
        ingredients = "",
        cookingAlgorithm = "",
        photoUri = ""
    )
}
