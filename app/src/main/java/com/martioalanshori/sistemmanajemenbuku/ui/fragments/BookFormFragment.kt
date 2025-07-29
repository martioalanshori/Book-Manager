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
import com.martioalanshori.sistemmanajemenbuku.ui.screens.BookFormScreen
import com.martioalanshori.sistemmanajemenbuku.data.BookRepository

/**
 * Fragment untuk form tambah/edit buku
 * 
 * Implementasi Fragment dengan Compose UI:
 * - Form input untuk data buku
 * - Validation dan error handling
 * - Save dan cancel functionality
 */
class BookFormFragment : Fragment() {

    private lateinit var viewModel: BookViewModel
    private var bookToEdit: Book? = null
    private var onSaveSuccess: (() -> Unit)? = null
    private var onCancel: (() -> Unit)? = null

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
                BookFormScreen(
                    viewModel = viewModel,
                    bookToEdit = bookToEdit,
                    onNavigateBack = { onCancel?.invoke() },
                    onSaveSuccess = { onSaveSuccess?.invoke() }
                )
            }
        }
    }

    /**
     * Set book to edit (for edit mode)
     */
    fun setBookToEdit(book: Book?) {
        this.bookToEdit = book
    }

    /**
     * Set callbacks
     */
    fun setCallbacks(
        onSaveSuccess: () -> Unit,
        onCancel: () -> Unit
    ) {
        this.onSaveSuccess = onSaveSuccess
        this.onCancel = onCancel
    }

    companion object {
        fun newInstance(): BookFormFragment {
            return BookFormFragment()
        }
    }
} 