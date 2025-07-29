package com.martioalanshori.sistemmanajemenbuku.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * SQLite Database Helper untuk operasi database langsung
 * 
 * Fitur:
 * - Create database dan tabel
 * - Insert sample data
 * - Get data dari SQLite
 * - Update data di SQLite
 * - Delete data dari SQLite
 * 
 * Implementasi SQLite Setup dan Android Project Connection
 */
class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "BookManager.db"
        private const val DATABASE_VERSION = 3 // Updated for ISBN field
        
        // Tabel Books
        private const val TABLE_BOOKS = "books"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_AUTHOR = "author"
        private const val COLUMN_ISBN = "isbn"
        private const val COLUMN_YEAR = "year"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_STATUS = "status"
        private const val COLUMN_DESCRIPTION = "description"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_BOOKS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_AUTHOR TEXT NOT NULL,
                $COLUMN_ISBN TEXT,
                $COLUMN_YEAR INTEGER NOT NULL,
                $COLUMN_CATEGORY TEXT NOT NULL,
                $COLUMN_STATUS TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT
            )
        """.trimIndent()
        
        db.execSQL(createTable)
        Log.d("SQLiteHelper", "Database created successfully")
        
        // Insert sample data
        insertSampleData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        when (oldVersion) {
            1 -> {
                // Migration from version 1 to 2
                Log.d("SQLiteHelper", "Migrating database from version 1 to 2")
            }
            2 -> {
                // Migration from version 2 to 3 - Add ISBN column
                Log.d("SQLiteHelper", "Migrating database from version 2 to 3 - Adding ISBN column")
                db.execSQL("ALTER TABLE $TABLE_BOOKS ADD COLUMN $COLUMN_ISBN TEXT")
            }
            else -> {
                // For any other version, recreate the database
                db.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKS")
                onCreate(db)
            }
        }
    }

    /**
     * Insert sample data ke database SQLite
     * Sample Get/Set Android SQLite Data
     */
    private fun insertSampleData(db: SQLiteDatabase) {
        val sampleBooks = listOf(
            Book(0, "Harry Potter and the Philosopher's Stone", "J.K. Rowling", "9780439708180", 1997, "Fantasy", BookStatus.AVAILABLE, "Buku pertama seri Harry Potter"),
            Book(0, "The Lord of the Rings", "J.R.R. Tolkien", "9780547928210", 1954, "Fantasy", BookStatus.AVAILABLE, "Epic fantasy novel"),
            Book(0, "To Kill a Mockingbird", "Harper Lee", "9780446310789", 1960, "Fiction", BookStatus.BORROWED, "Classic American novel"),
            Book(0, "1984", "George Orwell", "9780451524935", 1949, "Dystopian", BookStatus.AVAILABLE, "Dystopian social science fiction"),
            Book(0, "Pride and Prejudice", "Jane Austen", "9780141439518", 1813, "Romance", BookStatus.AVAILABLE, "Classic romance novel")
        )

        sampleBooks.forEach { book ->
            val values = ContentValues().apply {
                put(COLUMN_TITLE, book.title)
                put(COLUMN_AUTHOR, book.author)
                put(COLUMN_ISBN, book.isbn)
                put(COLUMN_YEAR, book.year)
                put(COLUMN_CATEGORY, book.category)
                put(COLUMN_STATUS, book.status.name)
                put(COLUMN_DESCRIPTION, book.description)
            }
            
            val id = db.insert(TABLE_BOOKS, null, values)
            Log.d("SQLiteHelper", "Inserted book: ${book.title} with ID: $id")
        }
    }

    /**
     * Get semua buku dari SQLite database
     * Sample Get Android SQLite Data
     */
    fun getAllBooks(): List<Book> {
        val books = mutableListOf<Book>()
        val db = this.readableDatabase
        
        val cursor = db.query(
            TABLE_BOOKS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_TITLE ASC"
        )

        with(cursor) {
            while (moveToNext()) {
                val book = Book(
                    id = getInt(getColumnIndexOrThrow(COLUMN_ID)),
                    title = getString(getColumnIndexOrThrow(COLUMN_TITLE)),
                    author = getString(getColumnIndexOrThrow(COLUMN_AUTHOR)),
                    isbn = getString(getColumnIndexOrThrow(COLUMN_ISBN)) ?: "",
                    year = getInt(getColumnIndexOrThrow(COLUMN_YEAR)),
                    category = getString(getColumnIndexOrThrow(COLUMN_CATEGORY)),
                    status = BookStatus.valueOf(getString(getColumnIndexOrThrow(COLUMN_STATUS))),
                    description = getString(getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                )
                books.add(book)
            }
        }
        cursor.close()
        return books
    }

    /**
     * Insert buku baru ke SQLite database
     * Sample Set Android SQLite Data
     */
    fun insertBook(book: Book): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, book.title)
            put(COLUMN_AUTHOR, book.author)
            put(COLUMN_ISBN, book.isbn)
            put(COLUMN_YEAR, book.year)
            put(COLUMN_CATEGORY, book.category)
            put(COLUMN_STATUS, book.status.name)
            put(COLUMN_DESCRIPTION, book.description)
        }
        
        Log.d("SQLiteHelper", "Inserting book: ${book.title}, author: ${book.author}, year: ${book.year}")
        val id = db.insert(TABLE_BOOKS, null, values)
        Log.d("SQLiteHelper", "Inserted book with ID: $id")
        return id
    }

    /**
     * Update buku di SQLite database
     * Edit Form implementation
     */
    fun updateBook(book: Book): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, book.title)
            put(COLUMN_AUTHOR, book.author)
            put(COLUMN_ISBN, book.isbn)
            put(COLUMN_YEAR, book.year)
            put(COLUMN_CATEGORY, book.category)
            put(COLUMN_STATUS, book.status.name)
            put(COLUMN_DESCRIPTION, book.description)
        }
        
        val rowsAffected = db.update(
            TABLE_BOOKS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(book.id.toString())
        )
        
        Log.d("SQLiteHelper", "Updated book with ID: ${book.id}, rows affected: $rowsAffected")
        return rowsAffected
    }

    /**
     * Delete buku dari SQLite database
     */
    fun deleteBook(bookId: Int): Int {
        val db = this.writableDatabase
        val rowsAffected = db.delete(
            TABLE_BOOKS,
            "$COLUMN_ID = ?",
            arrayOf(bookId.toString())
        )
        
        Log.d("SQLiteHelper", "Deleted book with ID: $bookId, rows affected: $rowsAffected")
        return rowsAffected
    }

    /**
     * Search buku berdasarkan query
     */
    fun searchBooks(query: String): List<Book> {
        val books = mutableListOf<Book>()
        val db = this.readableDatabase
        
        val selection = "$COLUMN_TITLE LIKE ? OR $COLUMN_AUTHOR LIKE ? OR $COLUMN_ISBN LIKE ?"
        val selectionArgs = arrayOf("%$query%", "%$query%", "%$query%")
        
        val cursor = db.query(
            TABLE_BOOKS,
            null,
            selection,
            selectionArgs,
            null,
            null,
            "$COLUMN_TITLE ASC"
        )

        with(cursor) {
            while (moveToNext()) {
                val book = Book(
                    id = getInt(getColumnIndexOrThrow(COLUMN_ID)),
                    title = getString(getColumnIndexOrThrow(COLUMN_TITLE)),
                    author = getString(getColumnIndexOrThrow(COLUMN_AUTHOR)),
                    year = getInt(getColumnIndexOrThrow(COLUMN_YEAR)),
                    category = getString(getColumnIndexOrThrow(COLUMN_CATEGORY)),
                    status = BookStatus.valueOf(getString(getColumnIndexOrThrow(COLUMN_STATUS))),
                    description = getString(getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                )
                books.add(book)
            }
        }
        cursor.close()
        return books
    }
} 