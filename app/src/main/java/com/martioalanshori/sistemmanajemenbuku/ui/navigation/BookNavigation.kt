package com.martioalanshori.sistemmanajemenbuku.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.martioalanshori.sistemmanajemenbuku.data.Book
import com.martioalanshori.sistemmanajemenbuku.ui.BookViewModel
import com.martioalanshori.sistemmanajemenbuku.ui.screens.BookDetailScreen
import com.martioalanshori.sistemmanajemenbuku.ui.screens.BookFormScreen
import com.martioalanshori.sistemmanajemenbuku.ui.screens.MainScreen

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object BookDetail : Screen("bookDetail/{bookId}") {
        fun createRoute(bookId: Int) = "bookDetail/$bookId"
    }
    object AddBook : Screen("addBook")
    object EditBook : Screen("editBook/{bookId}") {
        fun createRoute(bookId: Int) = "editBook/$bookId"
    }
}

@Composable
fun BookNavigation(
    navController: NavHostController,
    viewModel: BookViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                viewModel = viewModel,
                onNavigateToAdd = {
                    navController.navigate(Screen.AddBook.route)
                },
                onNavigateToEdit = { book ->
                    navController.navigate(Screen.EditBook.createRoute(book.id))
                },
                onNavigateToDetail = { book ->
                    navController.navigate(Screen.BookDetail.createRoute(book.id))
                }
            )
        }
        
        composable(
            route = Screen.BookDetail.route,
            arguments = listOf(
                navArgument("bookId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt("bookId") ?: return@composable
            val uiState by viewModel.uiState.collectAsState()
            val book = uiState.books.find { it.id == bookId }
            
            book?.let { foundBook ->
                BookDetailScreen(
                    book = foundBook,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToEdit = { bookToEdit ->
                        navController.navigate(Screen.EditBook.createRoute(bookToEdit.id))
                    }
                )
            }
        }
        
        composable(Screen.AddBook.route) {
            BookFormScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.EditBook.route,
            arguments = listOf(
                navArgument("bookId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt("bookId") ?: return@composable
            val uiState by viewModel.uiState.collectAsState()
            val book = uiState.books.find { it.id == bookId }
            
            BookFormScreen(
                viewModel = viewModel,
                bookToEdit = book,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
} 