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
import com.martioalanshori.sistemmanajemenbuku.ui.screens.MainScreen
import com.martioalanshori.sistemmanajemenbuku.data.BookRepository

/**
 * Fragment utama yang mengintegrasikan MainScreen dengan bottom navigation dan FAB
 * 
 * Fitur:
 * - Bottom navigation (Buku & Statistik)
 * - Floating Action Button untuk tambah buku
 * - Integrasi dengan FragmentManager untuk navigasi
 */
class MainFragment : Fragment() {

    private lateinit var viewModel: BookViewModel
    private var onNavigateToAdd: (() -> Unit)? = null
    private var onNavigateToEdit: ((Book) -> Unit)? = null
    private var onNavigateToDetail: ((Book) -> Unit)? = null

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
                MainScreen(
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
        onNavigateToEdit: (Book) -> Unit,
        onNavigateToDetail: (Book) -> Unit
    ) {
        this.onNavigateToAdd = onNavigateToAdd
        this.onNavigateToEdit = onNavigateToEdit
        this.onNavigateToDetail = onNavigateToDetail
    }

    /**
     * Refresh data
     */
    fun refreshData() {
        viewModel.refreshBooks()
    }

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }
} 