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
    val photoUrl: String
)