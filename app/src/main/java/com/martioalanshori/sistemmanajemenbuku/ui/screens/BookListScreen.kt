package com.martioalanshori.sistemmanajemenbuku.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.martioalanshori.sistemmanajemenbuku.R
import com.martioalanshori.sistemmanajemenbuku.data.Book
import com.martioalanshori.sistemmanajemenbuku.data.BookStatus
import com.martioalanshori.sistemmanajemenbuku.ui.BookViewModel
import com.martioalanshori.sistemmanajemenbuku.ui.components.BookItem
import com.martioalanshori.sistemmanajemenbuku.ui.components.DeleteConfirmationDialog
import com.martioalanshori.sistemmanajemenbuku.ui.components.QRScanner
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.automirrored.filled.MenuBook
import com.martioalanshori.sistemmanajemenbuku.ui.theme.AppColors
import androidx.compose.ui.graphics.Color

/**
 * Screen untuk menampilkan daftar buku dengan fitur pencarian dan filter
 * 
 * Fitur yang dioptimasi:
 * - Delete confirmation dialog
 * - Loading state
 * - Error handling
 * - Success message
 * - Better user experience
 * - QR Scanner untuk pencarian ISBN
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(
    viewModel: BookViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (Book) -> Unit,
    onNavigateToDetail: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf<BookStatus?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var bookToDelete by remember { mutableStateOf<Book?>(null) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showQRScanner by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Auto-clear messages
    LaunchedEffect(uiState.error, uiState.successMessage) {
        uiState.error?.let {
            snackbarHostState.showSnackbar("😅 $it")
            viewModel.clearError()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar("🎉 $it")
            viewModel.clearSuccessMessage()
        }
    }
    
    if (showQRScanner) {
        QRScanner(
            onISBNScanned = { isbn ->
                searchQuery = isbn
                viewModel.searchBooks(isbn)
                showQRScanner = false
            },
            onDismiss = {
                showQRScanner = false
            }
        )
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_logo_book),
                                contentDescription = "Logo Book Manager",
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Book Manager", fontWeight = FontWeight.Bold)
                        }
                    },
                    actions = {
                        IconButton(onClick = { showAboutDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Tentang Aplikasi"
                            )
                        }
                        IconButton(onClick = { viewModel.refreshBooks() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh"
                            )
                        }
                        IconButton(onClick = { showFilterDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Filter"
                            )
                        }
                    }
                )
            }
        ) { _ ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(top = 60.dp)
            ) {
                // Search Bar with QR Scanner Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { 
                            searchQuery = it
                            viewModel.searchBooks(it)
                        },
                        label = { Text("Cari buku favoritmu") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    
                    // QR Scanner Button - Made more prominent
                    Card(
                        modifier = Modifier
                            .size(52.dp)
                            .padding(top = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = AppColors.Primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        IconButton(
                            onClick = { showQRScanner = true },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scan ISBN Barcode",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
                
                // Filter Chips
                if (selectedStatus != null || selectedCategory != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        selectedStatus?.let { status ->
                            FilterChip(
                                selected = true,
                                onClick = { 
                                    selectedStatus = null
                                    viewModel.filterByStatus(null)
                                },
                                label = {
                                    Text(
                                        text = if (status == BookStatus.AVAILABLE) "Tersedia" else "Dipinjam"
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                        
                        selectedCategory?.let { category ->
                            FilterChip(
                                selected = true,
                                onClick = { 
                                    selectedCategory = null
                                    viewModel.filterByCategory(null)
                                },
                                label = { Text(category) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Main Content
                Box(modifier = Modifier.fillMaxSize()) {
                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else if (uiState.books.isEmpty()) {
                        // Empty state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = "Empty",
                                    modifier = Modifier.size(80.dp),
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    text = "Belum ada buku nih...",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Yuk mulai koleksi buku favoritmu!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = onNavigateToAdd,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Tambah Buku Pertama")
                                }
                            }
                        }
                    } else {
                        // Book list
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.books, key = { it.id }) { book ->
                                BookItem(
                                    book = book,
                                    onEditClick = onNavigateToEdit,
                                    onDeleteClick = { 
                                        bookToDelete = book
                                    },
                                    onItemClick = onNavigateToDetail
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Dialogs
        if (showFilterDialog) {
            FilterDialog(
                categories = uiState.categories,
                onStatusSelected = { status ->
                    selectedStatus = status
                    viewModel.filterByStatus(status)
                    showFilterDialog = false
                },
                onCategorySelected = { category ->
                    selectedCategory = category
                    viewModel.filterByCategory(category)
                    showFilterDialog = false
                },
                onDismiss = { showFilterDialog = false }
            )
        }
        
        DeleteConfirmationDialog(
            book = bookToDelete,
            onConfirm = {
                bookToDelete?.let { book ->
                    viewModel.deleteBook(book)
                }
                bookToDelete = null
            },
            onDismiss = {
                bookToDelete = null
            }
        )
        
        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo_book),
                        contentDescription = "Logo Book Manager",
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = {
                    Text("Book Manager", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Versi 1.0", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Aplikasi sederhana untuk mengelola koleksi buku pribadi.", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showAboutDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Oke, mengerti!")
                    }
                },
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
fun FilterDialog(
    categories: List<String>,
    onStatusSelected: (BookStatus?) -> Unit,
    onCategorySelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text("Filter Buku", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    text = "Status Buku",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterChip(
                        selected = false,
                        onClick = { onStatusSelected(BookStatus.AVAILABLE) },
                        label = { Text("📖 Tersedia") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                    FilterChip(
                        selected = false,
                        onClick = { onStatusSelected(BookStatus.BORROWED) },
                        label = { Text("📤 Dipinjam") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = "Kategori",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                
                if (categories.isEmpty()) {
                    Text(
                        "Belum ada kategori buku",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp)
                    ) {
                        items(categories) { category ->
                            TextButton(
                                onClick = { onCategorySelected(category) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Text(
                                    "📚 $category",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Start
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Tutup")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
} 