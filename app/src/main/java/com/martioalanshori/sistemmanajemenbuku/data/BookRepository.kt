package com.martioalanshori.sistemmanajemenbuku.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Repository untuk mengelola data buku dari database
 * 
 * Menggunakan SQLite Helper untuk operasi database langsung
 * Implementasi Group Task dan Listener pattern
 */
class BookRepository(
    private val bookDao: BookDao,
    private val sqliteHelper: SQLiteHelper
) {
    
    val allBooks: Flow<List<Book>> = flow {
        // Menggunakan SQLite Helper untuk get data
        val books = sqliteHelper.getAllBooks()
        emit(books)
    }.flowOn(Dispatchers.IO)
    
    val allCategories: Flow<List<String>> = flow {
        val existingCategories = sqliteHelper.getAllBooks()
            .map { it.category }
            .distinct()
            .sorted()
        
        // Tambahkan kategori default jika belum ada
        val defaultCategories = listOf("Fiksi", "Nonfiksi", "Pendidikan", "Teknologi", "Bisnis", "Sejarah", "Agama")
        
        val allCategories = (existingCategories + defaultCategories).distinct().sorted()
        emit(allCategories)
    }.flowOn(Dispatchers.IO)
    
    suspend fun insertBook(book: Book): Long {
        return with(Dispatchers.IO) {
            // Menggunakan SQLite Helper untuk insert
            Log.d("BookRepository", "Attempting to insert book: ${book.title}")
            val result = sqliteHelper.insertBook(book)
            Log.d("BookRepository", "Insert result: $result")
            result
        }
    }
    
    suspend fun updateBook(book: Book): Int {
        return with(Dispatchers.IO) {
            // Menggunakan SQLite Helper untuk update
            val result = sqliteHelper.updateBook(book)
            Log.d("BookRepository", "Update result: $result")
            result
        }
    }
    
    suspend fun deleteBook(book: Book) {
        with(Dispatchers.IO) {
            // Menggunakan SQLite Helper untuk delete
            sqliteHelper.deleteBook(book.id)
        }
    }
    
    suspend fun getAllBooks(): List<Book> {
        return with(Dispatchers.IO) {
            sqliteHelper.getAllBooks()
        }
    }
    
    suspend fun getAllCategories(): List<String> {
        return with(Dispatchers.IO) {
            val existingCategories = sqliteHelper.getAllBooks()
                .map { it.category }
                .distinct()
                .sorted()
            
            // Tambahkan kategori default jika belum ada
            val defaultCategories = listOf("Fiksi", "Nonfiksi", "Pendidikan", "Teknologi", "Bisnis", "Sejarah", "Agama")
            
            val allCategories = (existingCategories + defaultCategories).distinct().sorted()
            allCategories
        }
    }
    
    suspend fun getBookById(id: Int): Book? {
        return with(Dispatchers.IO) {
            sqliteHelper.getAllBooks().find { it.id == id }
        }
    }
    
    fun searchBooks(query: String): Flow<List<Book>> = flow {
        val books = if (query.isBlank()) {
            sqliteHelper.getAllBooks()
        } else {
            sqliteHelper.searchBooks(query)
        }
        emit(books)
    }.flowOn(Dispatchers.IO)
    
    fun getBooksByStatus(status: BookStatus): Flow<List<Book>> = flow {
        val books = sqliteHelper.getAllBooks().filter { it.status == status }
        emit(books)
    }.flowOn(Dispatchers.IO)
    
    fun getBooksByCategory(category: String): Flow<List<Book>> = flow {
        val books = sqliteHelper.getAllBooks().filter { it.category == category }
        emit(books)
    }.flowOn(Dispatchers.IO)
    
    companion object {
        @Volatile
        private var INSTANCE: BookRepository? = null
        
        fun getRepository(context: Context): BookRepository {
            return INSTANCE ?: synchronized(this) {
                val database = BookDatabase.getDatabase(context)
                val sqliteHelper = SQLiteHelper(context)
                val repository = BookRepository(database.bookDao(), sqliteHelper)
                INSTANCE = repository
                repository
            }
        }
    }
} 