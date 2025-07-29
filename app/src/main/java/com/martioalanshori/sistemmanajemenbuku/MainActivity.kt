package com.martioalanshori.sistemmanajemenbuku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import com.martioalanshori.sistemmanajemenbuku.ui.theme.SistemManajemenBukuTheme
import com.martioalanshori.sistemmanajemenbuku.ui.fragments.FragmentManager
import com.martioalanshori.sistemmanajemenbuku.ui.fragments.MainFragment
import com.martioalanshori.sistemmanajemenbuku.ui.fragments.BookFormFragment
import com.martioalanshori.sistemmanajemenbuku.ui.fragments.BookDetailFragment
import kotlinx.coroutines.delay

/**
 * MainActivity - Entry point aplikasi
 * 
 * Fitur:
 * - Splash screen
 * - Fragment management
 * - Navigation handling
 */
class MainActivity : FragmentActivity() {
    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize FragmentManager
        fragmentManager = FragmentManager(this)
        
        setContent {
            SistemManajemenBukuTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showSplash by remember { mutableStateOf(true) }
                    
                    if (showSplash) {
                        SplashScreenFragment {
                            showSplash = false
                            // Navigate to main fragment after splash
                            fragmentManager.navigateToMain()
                            // Setup callbacks after navigation
                            setupFragmentCallbacks()
                        }
                    } else {
                        FragmentContainerView()
                    }
                }
            }
        }
    }
    
    /**
     * Setup callbacks untuk komunikasi antar Fragment
     */
    private fun setupFragmentCallbacks() {
        // Setup callbacks dengan delay minimal untuk memastikan fragment sudah dibuat
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            if (!isFinishing && !isDestroyed) {
                // MainFragment callbacks
                val mainFragment = supportFragmentManager.findFragmentByTag(FragmentManager.FRAGMENT_MAIN) as? MainFragment
                mainFragment?.setNavigationCallbacks(
                    onNavigateToAdd = { 
                        if (!isFinishing && !isDestroyed) {
                            // Navigasi ke form tambah buku
                            fragmentManager.navigateToBookForm() 
                            // Setup callbacks untuk BookFormFragment yang baru dibuat
                            setupBookFormCallbacks()
                        }
                    },
                    onNavigateToEdit = { book -> 
                        if (!isFinishing && !isDestroyed) {
                            // Navigasi ke form edit buku
                            fragmentManager.navigateToBookForm(book) 
                            // Setup callbacks untuk BookFormFragment yang baru dibuat
                            setupBookFormCallbacks()
                        }
                    },
                    onNavigateToDetail = { book -> 
                        if (!isFinishing && !isDestroyed) {
                            // Navigasi ke detail buku
                            fragmentManager.navigateToBookDetail(book) 
                            // Setup callbacks untuk BookDetailFragment yang baru dibuat
                            setupBookDetailCallbacks()
                        }
                    }
                )
            }
        }, 50) // Reduced delay to 50ms for better responsiveness
    }
    
    /**
     * Setup callbacks untuk BookFormFragment
     */
    private fun setupBookFormCallbacks() {
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            if (!isFinishing && !isDestroyed) {
                val bookFormFragment = supportFragmentManager.findFragmentByTag(FragmentManager.FRAGMENT_BOOK_FORM) as? BookFormFragment
                bookFormFragment?.setCallbacks(
                    onSaveSuccess = { 
                        if (!isFinishing && !isDestroyed) {
                            // Kembali ke main setelah save berhasil
                            fragmentManager.goBack() 
                        }
                    },
                    onCancel = { 
                        if (!isFinishing && !isDestroyed) {
                            // Kembali ke main jika cancel
                            fragmentManager.goBack() 
                        }
                    }
                )
            }
        }, 30) // Reduced delay to 30ms
    }
    
    /**
     * Setup callbacks untuk BookDetailFragment
     */
    private fun setupBookDetailCallbacks() {
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            if (!isFinishing && !isDestroyed) {
                val bookDetailFragment = supportFragmentManager.findFragmentByTag(FragmentManager.FRAGMENT_BOOK_DETAIL) as? BookDetailFragment
                bookDetailFragment?.setCallbacks(
                    onEditClick = { book -> 
                        if (!isFinishing && !isDestroyed) {
                            // Navigasi ke form edit
                            fragmentManager.navigateToBookForm(book) 
                            // Setup callbacks untuk BookFormFragment yang baru dibuat
                            setupBookFormCallbacks()
                        }
                    },
                    onDeleteClick = { _ -> 
                        if (!isFinishing && !isDestroyed) {
                            // Handle delete dan kembali ke main
                            // viewModel.deleteBook(book)
                            fragmentManager.goBack() 
                        }
                    },
                    onBackClick = { 
                        if (!isFinishing && !isDestroyed) {
                            // Kembali ke main
                            fragmentManager.goBack() 
                        }
                    }
                )
            }
        }, 30) // Reduced delay to 30ms
    }
}

@Composable
fun SplashScreenFragment(onFinish: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onFinish()
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo_book),
                contentDescription = "Logo Book Manager",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Dibuat oleh Muhammad Martio Alanshori",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun FragmentContainerView() {
    // Fragment content akan di-inject oleh FragmentManager
    // Fragment menggunakan Compose UI di dalamnya
    Box(modifier = Modifier.fillMaxSize()) {
        // Fragment content akan ditampilkan di sini
    }
}