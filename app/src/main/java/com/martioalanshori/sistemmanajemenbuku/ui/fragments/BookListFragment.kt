package com.martioalanshori.sistemmanajemenbuku.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.martioalanshori.sistemmanajemenbuku.ui.BookViewModel
import com.martioalanshori.sistemmanajemenbuku.ui.BookViewModelFactory
import com.martioalanshori.sistemmanajemenbuku.ui.screens.BookListScreen
import com.martioalanshori.sistemmanajemenbuku.data.BookRepository

/**
 * Fragment untuk menampilkan daftar buku
 * 
 * Implementasi Fragment dengan Compose UI:
 * - Menggunakan Fragment sebagai container
 * - Compose UI sebagai content
 * - ViewModel untuk business logic
 * - Navigation callback untuk interaksi
 */
class BookListFragment : Fragment() {

    private lateinit var viewModel: BookViewModel
    private var onNavigateToAdd: (() -> Unit)? = null
    private var onNavigateToEdit: ((com.martioalanshori.sistemmanajemenbuku.data.Book) -> Unit)? = null
    private var onNavigateToDetail: ((com.martioalanshori.sistemmanajemenbuku.data.Book) -> Unit)? = null

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
                BookListScreen(
                    viewModel = viewModel,
                    onNavigateToAdd = { onNavigateToAdd?.invoke() },
                    onNavigateToEdit = { book -> onNavigateToEdit?.invoke(book) },
                    onNavigateToDetail = { book -> onNavigateToDetail?.invoke(book) }
                )
            }
        }
    }

    /**
     * Set navigation callbacks
     */
    fun setNavigationCallbacks(
        onNavigateToAdd: () -> Unit,
        onNavigateToEdit: (com.martioalanshori.sistemmanajemenbuku.data.Book) -> Unit,
        onNavigateToDetail: (com.martioalanshori.sistemmanajemenbuku.data.Book) -> Unit
    ) {
        this.onNavigateToAdd = onNavigateToAdd
        this.onNavigateToEdit = onNavigateToEdit
        this.onNavigateToDetail = onNavigateToDetail
    }

    /**
     * Refresh book list
     */
    fun refreshBooks() {
        viewModel.refreshBooks()
    }

    companion object {
        fun newInstance(): BookListFragment {
            return BookListFragment()
        }
    }
} 