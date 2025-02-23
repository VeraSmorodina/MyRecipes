package com.vsmorodina.myrecipes.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(//data переопределяет методы икволс, хэшкод, тустринг(возвращает объект в виде строки) и добавляет метод копи, который возвращяет копию объекта
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "photo_url")
    val photoUrl: String,

    @ColumnInfo(name = "is_default")
    val isDefault: Boolean = false
)
// Цезарь   помидоры огурцы   шаг 1   шаг 2