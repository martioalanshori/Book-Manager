package com.martioalanshori.sistemmanajemenbuku.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.martioalanshori.sistemmanajemenbuku.data.Book
import com.martioalanshori.sistemmanajemenbuku.ui.BookViewModel
import com.martioalanshori.sistemmanajemenbuku.ui.BookViewModelFactory
import com.martioalanshori.sistemmanajemenbuku.ui.screens.BookDetailScreen
import com.martioalanshori.sistemmanajemenbuku.data.BookRepository

/**
 * Fragment untuk menampilkan detail buku
 * 
 * Implementasi Fragment dengan Compose UI:
 * - Detail informasi buku
 * - Edit dan delete options
 * - Status management
 */
class BookDetailFragment : Fragment() {

    private lateinit var viewModel: BookViewModel
    private var book: Book? = null
    private var onEditClick: ((Book) -> Unit)? = null
    private var onDeleteClick: ((Book) -> Unit)? = null
    private var onBackClick: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize ViewModel
        val repository = BookRepository.getRepository(requireContext())
        viewModel = BookViewModelFactory(repository).create(BookViewModel::class.java)

        return ComposeView(requireContext()).apply {
            setContent {
                book?.let { bookData ->
                    BookDetailScreen(
                        book = bookData,
                        onNavigateBack = { onBackClick?.invoke() },
                        onNavigateToEdit = { book -> onEditClick?.invoke(book) }
                    )
                }
            }
        }
    }

    /**
     * Set book to display
     */
    fun setBook(book: Book?) {
        this.book = book
    }

    /**
     * Set callbacks
     */
    fun setCallbacks(
        onEditClick: (Book) -> Unit,
        onDeleteClick: (Book) -> Unit,
        onBackClick: () -> Unit
    ) {
        this.onEditClick = onEditClick
        this.onDeleteClick = onDeleteClick
        this.onBackClick = onBackClick
    }

    companion object {
        fun newInstance(): BookDetailFragment {
            return BookDetailFragment()
        }
    }
} 