package com.martioalanshori.sistemmanajemenbuku.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.martioalanshori.sistemmanajemenbuku.data.Book

/**
 * Dialog konfirmasi untuk delete buku
 * 
 * Fitur:
 * - Konfirmasi sebelum delete
 * - Menampilkan judul buku yang akan dihapus
 * - Warning icon untuk perhatian user
 * - Cancel dan Delete button
 */
@Composable
fun DeleteConfirmationDialog(
    book: Book?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (book != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(
                    "Hapus Buku?",
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Kamu yakin mau hapus buku \"${book.title}\"?\n\n" +
                            "Kalau udah dihapus, nggak bisa dikembalikan lagi lho... 😅",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hapus")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = onDismiss,
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                        contentColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Batal")
                }
            },
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        )
    }
} 