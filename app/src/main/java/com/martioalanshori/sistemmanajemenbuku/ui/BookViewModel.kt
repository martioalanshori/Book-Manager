package com.martioalanshori.sistemmanajemenbuku.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.martioalanshori.sistemmanajemenbuku.data.Book
import com.martioalanshori.sistemmanajemenbuku.data.BookRepository
import com.martioalanshori.sistemmanajemenbuku.data.BookStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel untuk state management dan logika aplikasi
 * 
 * Implementasi Group Task dan Listener pattern:
 * - Menggunakan Repository pattern
 * - StateFlow untuk reactive UI updates
 * - Coroutines untuk async operations
 * - Better error handling untuk delete operations
 */
class BookViewModel(private val repository: BookRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow(BookUiState())
    val uiState: StateFlow<BookUiState> = _uiState.asStateFlow()
    
    init {
        loadBooks()
        loadCategories()
    }
    
    private fun loadBooks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val books = repository.getAllBooks()
                _uiState.update { 
                    it.copy(
                        books = books,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Gagal memuat data buku: ${e.message}"
                    )
                }
            }
        }
    }
    
    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val categories = repository.getAllCategories()
                _uiState.update { it.copy(categories = categories) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Gagal memuat kategori: ${e.message}")
                }
            }
        }
    }
    
    fun addBook(book: Book) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val result = repository.insertBook(book)
                if (result > 0) {
                    // Success - reload books and show success message
                    val updatedBooks = repository.getAllBooks()
                    _uiState.update { 
                        it.copy(
                            books = updatedBooks,
                            isLoading = false,
                            successMessage = "Buku \"${book.title}\" berhasil ditambahkan"
                        )
                    }
                } else {
                    // Failed to insert
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Gagal menambah buku: Database error"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Gagal menambah buku: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun updateBook(book: Book) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val result = repository.updateBook(book)
                if (result > 0) {
                    // Success - reload books and show success message
                    val updatedBooks = repository.getAllBooks()
                    _uiState.update { 
                        it.copy(
                            books = updatedBooks,
                            isLoading = false,
                            successMessage = "Buku \"${book.title}\" berhasil diupdate"
                        )
                    }
                } else {
                    // Failed to update
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Gagal mengupdate buku: Database error"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Gagal mengupdate buku: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun deleteBook(book: Book) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.deleteBook(book)
                // Reload books after delete
                val updatedBooks = repository.getAllBooks()
                _uiState.update { 
                    it.copy(
                        books = updatedBooks,
                        isLoading = false,
                        error = null,
                        successMessage = "Buku \"${book.title}\" berhasil dihapus"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Gagal menghapus buku: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun searchBooks(query: String) {
        viewModelScope.launch {
            try {
                val books = if (query.isBlank()) {
                    repository.getAllBooks()
                } else {
                    repository.searchBooks(query).first()
                }
                _uiState.update { it.copy(books = books) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Gagal mencari buku: ${e.message}")
                }
            }
        }
    }
    
    fun filterByStatus(status: BookStatus?) {
        viewModelScope.launch {
            try {
                val books = if (status == null) {
                    repository.getAllBooks()
                } else {
                    repository.getBooksByStatus(status).first()
                }
                _uiState.update { it.copy(books = books) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Gagal filter berdasarkan status: ${e.message}")
                }
            }
        }
    }
    
    fun filterByCategory(category: String?) {
        viewModelScope.launch {
            try {
                val books = if (category == null || category == "All") {
                    repository.getAllBooks()
                } else {
                    repository.getBooksByCategory(category).first()
                }
                _uiState.update { it.copy(books = books) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Gagal filter berdasarkan kategori: ${e.message}")
                }
            }
        }
    }
    
    fun setSelectedBook(book: Book?) {
        _uiState.update { it.copy(selectedBook = book) }
    }
    
    fun clearSelectedBook() {
        _uiState.update { it.copy(selectedBook = null) }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
    
    fun refreshBooks() {
        viewModelScope.launch {
            try {
                val books = repository.getAllBooks()
                _uiState.update { it.copy(books = books) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Gagal memuat data buku: ${e.message}")
                }
            }
        }
    }
}

data class BookUiState(
    val books: List<Book> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedBook: Book? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class BookViewModelFactory(private val repository: BookRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 