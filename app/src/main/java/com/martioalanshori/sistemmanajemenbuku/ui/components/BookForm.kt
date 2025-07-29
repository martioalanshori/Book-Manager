package com.martioalanshori.sistemmanajemenbuku.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.martioalanshori.sistemmanajemenbuku.data.Book
import com.martioalanshori.sistemmanajemenbuku.data.BookStatus
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.foundation.clickable
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookForm(
    book: Book?,
    categories: List<String>,
    scannedISBN: String = "",
    onSave: (Book) -> Unit,
    onCancel: () -> Unit,
    onScanISBN: () -> Unit,
    onISBNCleared: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    var title by remember { mutableStateOf(book?.title ?: "") }
    var author by remember { mutableStateOf(book?.author ?: "") }
    var isbn by remember { mutableStateOf(book?.isbn ?: "") }
    var year by remember { mutableStateOf(book?.year?.toString() ?: "") }
    var category by remember { mutableStateOf(book?.category ?: "") }
    var status by remember { mutableStateOf(book?.status ?: BookStatus.AVAILABLE) }
    var description by remember { mutableStateOf(book?.description ?: "") }
    
    var titleError by remember { mutableStateOf(false) }
    var authorError by remember { mutableStateOf(false) }
    var yearError by remember { mutableStateOf(false) }
    var categoryError by remember { mutableStateOf(false) }
    
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    
    // Update ISBN when scanned ISBN is available
    LaunchedEffect(scannedISBN) {
        if (scannedISBN.isNotEmpty() && isbn.isEmpty()) {
            isbn = scannedISBN
        }
    }
    
    // Kategori default yang selalu tersedia
    val defaultCategories = listOf("Fiksi", "Nonfiksi", "Pendidikan", "Teknologi", "Bisnis", "Sejarah", "Agama")
    val allCategories = (categories + defaultCategories).distinct().sorted()
    val maxChipPerRow = 4
    val chipRows = allCategories.chunked(maxChipPerRow)
    
    // Validasi real-time
    fun validateTitle() { titleError = title.isBlank() }
    fun validateAuthor() { authorError = author.isBlank() }
    fun validateYear() { yearError = year.isBlank() || year.toIntOrNull() == null }
    fun validateCategory() { categoryError = category.isBlank() }
    
    val isFormValid = remember(title, author, year, category, titleError, authorError, yearError, categoryError) {
        !titleError && !authorError && !yearError && !categoryError &&
        title.isNotBlank() && author.isNotBlank() && year.isNotBlank() && year.toIntOrNull() != null && category.isNotBlank()
    }
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = if (book == null) "Tambah Buku Baru" else "Edit Buku",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 18.dp)
        )
        
        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                if (titleError) validateTitle()
            },
            label = { Text("Judul Buku") },
            isError = titleError,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            singleLine = true,
            supportingText = { if (titleError) Text("Judul buku wajib diisi", color = MaterialTheme.colorScheme.error) }
        )
        Spacer(modifier = Modifier.height(10.dp))
        
        OutlinedTextField(
            value = author,
            onValueChange = {
                author = it
                if (authorError) validateAuthor()
            },
            label = { Text("Penulis") },
            isError = authorError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = { if (authorError) Text("Nama penulis wajib diisi", color = MaterialTheme.colorScheme.error) }
        )
        Spacer(modifier = Modifier.height(10.dp))
        
        // ISBN Field with QR Scanner
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = isbn,
                    onValueChange = { isbn = it },
                    label = { Text("ISBN (Opsional)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text("978-0-123456-78-9") },
                    supportingText = {
                        if (scannedISBN.isNotEmpty() && isbn == scannedISBN) {
                            Text(
                                "ISBN berhasil di-scan",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
                
                IconButton(
                    onClick = onScanISBN,
                    modifier = Modifier
                        .size(56.dp)
                        .padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Scan ISBN Barcode",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Show clear button if ISBN was scanned
            if (scannedISBN.isNotEmpty() && isbn == scannedISBN) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            isbn = ""
                            onISBNCleared()
                        }
                    ) {
                        Text("Hapus ISBN yang di-scan")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        
        OutlinedTextField(
            value = year,
            onValueChange = {
                year = it
                if (yearError) validateYear()
            },
            label = { Text("Tahun Terbit") },
            isError = yearError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = { if (yearError) Text("Tahun harus berupa angka", color = MaterialTheme.colorScheme.error) }
        )
        Spacer(modifier = Modifier.height(10.dp))
        
        Text(
            text = "Kategori",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            chipRows.forEach { rowCategories ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    rowCategories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = {
                                category = cat
                                categoryError = false
                            },
                            label = { Text(text = cat, maxLines = 1, modifier = Modifier.padding(horizontal = 4.dp)) },
                            modifier = Modifier.padding(vertical = 2.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary,
                                selectedLeadingIconColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        
        Text(
            text = "Status",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BookStatus.values().forEach { bookStatus ->
                FilterChip(
                    selected = status == bookStatus,
                    onClick = { status = bookStatus },
                    label = {
                        Text(
                            text = if (bookStatus == BookStatus.AVAILABLE) "Tersedia" else "Dipinjam"
                        )
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Deskripsi (Opsional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )
        Spacer(modifier = Modifier.height(18.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = {
                    focusManager.clearFocus()
                    onCancel()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Batal")
            }
            Button(
                onClick = {
                    validateTitle(); validateAuthor(); validateYear(); validateCategory()
                    if (isFormValid) {
                        focusManager.clearFocus()
                        val newBook = Book(
                            id = book?.id ?: 0,
                            title = title.trim(),
                            author = author.trim(),
                            isbn = isbn.trim(),
                            year = year.toIntOrNull() ?: 0,
                            category = category.trim(),
                            status = status,
                            description = description.trim()
                        )
                        onSave(newBook)
                    }
                },
                enabled = isFormValid && !isLoading,
                modifier = Modifier.weight(1f)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Menyimpan...")
                } else {
                    Text(if (book == null) "Simpan" else "Update")
                }
            }
        }
    }
} 