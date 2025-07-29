package com.martioalanshori.sistemmanajemenbuku.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.martioalanshori.sistemmanajemenbuku.data.Book
import com.martioalanshori.sistemmanajemenbuku.ui.BookViewModel
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.graphicsLayer
import com.martioalanshori.sistemmanajemenbuku.ui.screens.MapsScreen

enum class BottomNavItem(val route: String, val title: String, val icon: @Composable () -> Unit) {
    Books("books", "Buku", {
        Icon(Icons.AutoMirrored.Filled.LibraryBooks, contentDescription = "Buku")
    }),
    Statistics("statistics", "Statistik", {
        Icon(Icons.Default.Analytics, contentDescription = "Statistik")
    }),
    Maps("maps", "Maps", {
        Icon(Icons.Default.LocationOn, contentDescription = "Maps")
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: BookViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (Book) -> Unit,
    onNavigateToDetail: (Book) -> Unit
) {
    var selectedTab by remember { mutableStateOf(BottomNavItem.Books) }
    
    // FAB animation state
    var fabPressed by remember { mutableStateOf(false) }
    val fabScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (fabPressed) 0.9f else 1f,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 150), label = "fabScale"
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = BottomNavItem.Books.icon,
                    label = { Text(BottomNavItem.Books.title) },
                    selected = selectedTab == BottomNavItem.Books,
                    onClick = { selectedTab = BottomNavItem.Books }
                )
                NavigationBarItem(
                    icon = BottomNavItem.Statistics.icon,
                    label = { Text(BottomNavItem.Statistics.title) },
                    selected = selectedTab == BottomNavItem.Statistics,
                    onClick = { selectedTab = BottomNavItem.Statistics }
                )
                NavigationBarItem(
                    icon = BottomNavItem.Maps.icon,
                    label = { Text(BottomNavItem.Maps.title) },
                    selected = selectedTab == BottomNavItem.Maps,
                    onClick = { selectedTab = BottomNavItem.Maps }
                )
            }
        },
        floatingActionButton = {
            if (selectedTab == BottomNavItem.Books) {
                FloatingActionButton(
                    onClick = {
                        fabPressed = true
                        onNavigateToAdd()
                        fabPressed = false
                    },
                    modifier = Modifier.graphicsLayer { scaleX = fabScale; scaleY = fabScale }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Buku"
                    )
                }
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            BottomNavItem.Books -> {
                BookListScreen(
                    viewModel = viewModel,
                    onNavigateToAdd = onNavigateToAdd,
                    onNavigateToEdit = onNavigateToEdit,
                    onNavigateToDetail = onNavigateToDetail,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            BottomNavItem.Statistics -> {
                StatisticsScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            BottomNavItem.Maps -> {
                MapsScreen(
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
} 