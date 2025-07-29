package com.martioalanshori.sistemmanajemenbuku.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.martioalanshori.sistemmanajemenbuku.data.Book

/**
 * Pop Up Menu untuk item buku
 * 
 * Fitur:
 * - Edit: Membuka form edit buku
 * - Delete: Menghapus buku dari database SQLite
 * - Detail: Menampilkan detail buku
 * 
 * Listener pattern digunakan untuk callback ke parent component
 */
@Composable
fun BookItemMenu(
    book: Book,
    onEditClick: (Book) -> Unit,
    onDeleteClick: (Book) -> Unit,
    onDetailClick: (Book) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More options"
        )
    }
    
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { Text("Detail") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Detail"
                )
            },
            onClick = {
                onDetailClick(book)
                expanded = false
            }
        )
        
        DropdownMenuItem(
            text = { Text("Edit") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit"
                )
            },
            onClick = {
                onEditClick(book)
                expanded = false
            }
        )
        
        DropdownMenuItem(
            text = { Text("Delete") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete"
                )
            },
            onClick = {
                onDeleteClick(book)
                expanded = false
            }
        )
    }
} 