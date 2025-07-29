package com.martioalanshori.sistemmanajemenbuku.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.martioalanshori.sistemmanajemenbuku.data.Book

/**
 * Fragment Manager untuk mengelola navigasi antar Fragment
 * 
 * Implementasi Fragment Management:
 * - Fragment transaction handling
 * - Back stack management
 * - Fragment lifecycle management
 */
class FragmentManager(private val activity: FragmentActivity) {

    companion object {
        const val FRAGMENT_MAIN = "main"
        const val FRAGMENT_BOOK_LIST = "book_list"
        const val FRAGMENT_BOOK_FORM = "book_form"
        const val FRAGMENT_BOOK_DETAIL = "book_detail"
        const val FRAGMENT_STATISTICS = "statistics"
    }

    /**
     * Navigate to main fragment (with bottom navigation)
     */
    fun navigateToMain() {
        val fragment = MainFragment.newInstance()
        replaceFragment(fragment, FRAGMENT_MAIN)
    }

    /**
     * Navigate to book list fragment
     */
    fun navigateToBookList() {
        val fragment = BookListFragment.newInstance()
        replaceFragment(fragment, FRAGMENT_BOOK_LIST)
    }

    /**
     * Navigate to book form fragment (add mode)
     */
    fun navigateToBookForm() {
        val fragment = BookFormFragment.newInstance()
        replaceFragment(fragment, FRAGMENT_BOOK_FORM)
    }

    /**
     * Navigate to book form fragment (edit mode)
     */
    fun navigateToBookForm(book: Book) {
        val fragment = BookFormFragment.newInstance()
        fragment.setBookToEdit(book)
        replaceFragment(fragment, FRAGMENT_BOOK_FORM)
    }

    /**
     * Navigate to book detail fragment
     */
    fun navigateToBookDetail(book: Book) {
        val fragment = BookDetailFragment.newInstance()
        fragment.setBook(book)
        replaceFragment(fragment, FRAGMENT_BOOK_DETAIL)
    }

    /**
     * Navigate to statistics fragment
     */
    fun navigateToStatistics() {
        val fragment = StatisticsFragment.newInstance()
        replaceFragment(fragment, FRAGMENT_STATISTICS)
    }

    /**
     * Replace current fragment with new fragment
     */
    private fun replaceFragment(fragment: Fragment, tag: String) {
        try {
            activity.supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, fragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss() // Use commitAllowingStateLoss for better performance
        } catch (e: Exception) {
            // Fallback to immediate commit if state loss occurs
            try {
                activity.supportFragmentManager.beginTransaction()
                    .replace(android.R.id.content, fragment, tag)
                    .addToBackStack(tag)
                    .commit()
            } catch (e2: Exception) {
                // Log error but don't crash
                e2.printStackTrace()
            }
        }
    }

    /**
     * Get current fragment
     */
    fun getCurrentFragment(): Fragment? {
        return try {
            activity.supportFragmentManager.findFragmentById(android.R.id.content)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Go back to previous fragment
     */
    fun goBack(): Boolean {
        return try {
            if (activity.supportFragmentManager.backStackEntryCount > 1) {
                activity.supportFragmentManager.popBackStack()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            // Fallback to immediate pop if state loss occurs
            try {
                if (activity.supportFragmentManager.backStackEntryCount > 1) {
                    activity.supportFragmentManager.popBackStackImmediate()
                    true
                } else {
                    false
                }
            } catch (e2: Exception) {
                e2.printStackTrace()
                false
            }
        }
    }
} 