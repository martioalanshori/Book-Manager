package com.martioalanshori.sistemmanajemenbuku.data

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

/**
 * Service untuk ISBN lookup menggunakan OpenLibrary API
 * 
 * Fitur Internet + QR Code:
 * - ISBN lookup dari API
 * - Book data fetching
 * - Error handling
 */
interface ISBNService {
    
    @GET("api/books")
    suspend fun getBookByISBN(
        @Query("bibkeys") isbn: String,
        @Query("format") format: String = "json",
        @Query("jscmd") jscmd: String = "data"
    ): Response<Map<String, BookInfo>>
}

/**
 * Data class untuk book info dari API
 */
data class BookInfo(
    val title: String? = null,
    val authors: List<Author>? = null,
    val publishers: List<Publisher>? = null,
    val publish_date: String? = null,
    val number_of_pages: Int? = null,
    val subjects: List<Subject>? = null,
    val cover: Cover? = null,
    val url: String? = null
)

data class Author(
    val name: String,
    val url: String? = null
)

data class Publisher(
    val name: String
)

data class Subject(
    val name: String,
    val url: String? = null
)

data class Cover(
    val small: String? = null,
    val medium: String? = null,
    val large: String? = null
)

/**
 * Repository untuk ISBN operations
 */
class ISBNRepository(private val isbnService: ISBNService) {
    
    /**
     * Get book info by ISBN
     */
    suspend fun getBookByISBN(isbn: String): Result<BookInfo> {
        return try {
            val response = isbnService.getBookByISBN("ISBN:$isbn")
            if (response.isSuccessful) {
                val bookData = response.body()
                val bookInfo = bookData?.values?.firstOrNull()
                if (bookInfo != null) {
                    Result.success(bookInfo)
                } else {
                    Result.failure(Exception("Book not found for ISBN: $isbn"))
                }
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Convert BookInfo to Book entity
     */
    fun convertToBook(bookInfo: BookInfo, isbn: String): Book {
        return Book(
            id = 0, // Will be set by database
            title = bookInfo.title ?: "Unknown Title",
            author = bookInfo.authors?.firstOrNull()?.name ?: "Unknown Author",
            isbn = isbn,
            year = bookInfo.publish_date?.substringBefore("-")?.toIntOrNull() ?: 0,
            category = bookInfo.subjects?.firstOrNull()?.name ?: "General",
            status = BookStatus.AVAILABLE,
            description = bookInfo.subjects?.joinToString(", ") { it.name } ?: ""
        )
    }
} 