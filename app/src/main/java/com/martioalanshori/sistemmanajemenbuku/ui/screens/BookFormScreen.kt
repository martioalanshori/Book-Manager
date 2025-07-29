package com.martioalanshori.sistemmanajemenbuku.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.martioalanshori.sistemmanajemenbuku.data.Book
import com.martioalanshori.sistemmanajemenbuku.ui.BookViewModel
import com.martioalanshori.sistemmanajemenbuku.ui.components.BookForm
import com.martioalanshori.sistemmanajemenbuku.ui.components.QRScanner


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookFormScreen(
    viewModel: BookViewModel,
    bookToEdit: Book? = null,
    onNavigateBack: () -> Unit,
    onSaveSuccess: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    var showQRScanner by remember { mutableStateOf(false) }
    var scannedISBN by remember { mutableStateOf("") }
    
    // Handle success message and navigation
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { _ ->
            // Wait a bit for the message to be shown, then navigate
            kotlinx.coroutines.delay(500)
            onSaveSuccess?.invoke() ?: onNavigateBack()
            viewModel.clearSuccessMessage()
        }
    }
    
    if (showQRScanner) {
        QRScanner(
            onISBNScanned = { isbn ->
                scannedISBN = isbn
                showQRScanner = false
            },
            onDismiss = {
                showQRScanner = false
            }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            text = if (bookToEdit == null) "Tambah Buku" else "Edit Buku",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            BookForm(
                book = bookToEdit,
                categories = uiState.categories,
                scannedISBN = scannedISBN,
                onSave = { book ->
                    if (bookToEdit == null) {
                        viewModel.addBook(book)
                    } else {
                        viewModel.updateBook(book)
                    }
                    // Don't call refreshBooks() here - it's handled in addBook/updateBook
                    // Don't navigate immediately - wait for success message
                },
                onCancel = onNavigateBack,
                onScanISBN = { showQRScanner = true },
                onISBNCleared = { scannedISBN = "" },
                isLoading = uiState.isLoading,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }
} 