package com.vsmorodina.myrecipes.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vsmorodina.myrecipes.domain.entity.Category

@Entity(tableName = "categories")
data class CategoryEntity(//data переопределяет методы икволс, хэшкод, тустринг(возвращает объект в виде строки) и добавляет метод копи, который возвращяет копию объекта
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "photo_uri")
    val photoUri: String,

    @ColumnInfo(name = "is_default")
    val isDefault: Boolean = false,

    @ColumnInfo(name = "type")
    val type: CategoryType = CategoryType.NONE
) {
    fun toModel() = Category(
        id = id,
        name = name,
        photoUri = photoUri,
        isDefault = isDefault,
        type = type
    )
}

enum class CategoryType {
    NONE,
    SOUPS,
    SALADS,
    BAKING,
    APPETIZERS,
    MEAT,
    GARNISH,
    BEVERAGES,
    SAUCES
}