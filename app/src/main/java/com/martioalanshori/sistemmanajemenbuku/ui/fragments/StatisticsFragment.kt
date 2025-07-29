package com.martioalanshori.sistemmanajemenbuku.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.martioalanshori.sistemmanajemenbuku.ui.BookViewModel
import com.martioalanshori.sistemmanajemenbuku.ui.BookViewModelFactory
import com.martioalanshori.sistemmanajemenbuku.ui.screens.StatisticsScreen
import com.martioalanshori.sistemmanajemenbuku.data.BookRepository

/**
 * Fragment untuk menampilkan statistik buku
 * 
 * Implementasi Fragment dengan Compose UI:
 * - Chart dan grafik statistik
 * - Summary data buku
 * - Analytics dashboard
 */
class StatisticsFragment : Fragment() {

    private lateinit var viewModel: BookViewModel
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
                StatisticsScreen(
                    viewModel = viewModel
                )
            }
        }
    }

    /**
     * Set callbacks
     */
    fun setCallbacks(
        onBackClick: () -> Unit
    ) {
        this.onBackClick = onBackClick
    }

    /**
     * Refresh statistics
     */
    fun refreshStatistics() {
        viewModel.refreshBooks()
    }

    companion object {
        fun newInstance(): StatisticsFragment {
            return StatisticsFragment()
        }
    }
} 