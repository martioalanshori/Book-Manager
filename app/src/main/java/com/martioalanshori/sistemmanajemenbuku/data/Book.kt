package com.martioalanshori.sistemmanajemenbuku.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val author: String,
    val isbn: String = "",
    val year: Int,
    val category: String,
    val status: BookStatus,
    val description: String
)

enum class BookStatus {
    AVAILABLE,
    BORROWED
} 