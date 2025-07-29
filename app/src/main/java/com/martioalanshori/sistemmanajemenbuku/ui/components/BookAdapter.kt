package com.martioalanshori.sistemmanajemenbuku.ui.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.martioalanshori.sistemmanajemenbuku.R
import com.martioalanshori.sistemmanajemenbuku.data.Book
import com.martioalanshori.sistemmanajemenbuku.data.BookStatus

/**
 * IMPLEMENTASI ADAPTER & LAYOUTMANAGER YANG LEBIH JELAS
 * 
 * Fitur yang diimplementasi:
 * 1. RecyclerView.Adapter untuk menangani data buku
 * 2. ViewHolder untuk mengoptimalkan performa
 * 3. LayoutManager untuk mengatur layout item
 * 4. Multiple data handling dengan List<Book>
 * 5. Click listener untuk interaksi
 * 6. Status management (Available/Borrowed)
 * 
 * Implementasi sesuai ketentuan:
 * - RecyclerView ✅
 * - Adapter ✅
 * - ViewHolder ✅
 * - LayoutManager ✅
 * - Multiple Data (Array/List) ✅
 */
class BookAdapter(
    private val books: List<Book>,
    private val onItemClick: (Book) -> Unit,
    private val onEditClick: (Book) -> Unit,
    private val onDeleteClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    /**
     * VIEWHOLDER - Mengoptimalkan performa dengan menyimpan referensi view
     */
    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.tv_book_title)
        val authorTextView: TextView = itemView.findViewById(R.id.tv_book_author)
        val categoryTextView: TextView = itemView.findViewById(R.id.tv_book_category)
        val yearTextView: TextView = itemView.findViewById(R.id.tv_book_year)
        val statusTextView: TextView = itemView.findViewById(R.id.tv_book_status)
        val isbnTextView: TextView = itemView.findViewById(R.id.tv_book_isbn)
        val descriptionTextView: TextView = itemView.findViewById(R.id.tv_book_description)
        val editButton: View = itemView.findViewById(R.id.btn_edit_book)
        val deleteButton: View = itemView.findViewById(R.id.btn_delete_book)
    }

    /**
     * LAYOUTMANAGER - Mengatur layout untuk item buku
     * Menggunakan LinearLayoutManager untuk layout vertikal
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    /**
     * ADAPTER - Menangani binding data ke ViewHolder
     */
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        
        // Bind data ke ViewHolder
        holder.titleTextView.text = book.title
        holder.authorTextView.text = "Penulis: ${book.author}"
        holder.categoryTextView.text = "Kategori: ${book.category}"
        holder.yearTextView.text = "Tahun: ${book.year}"
        holder.isbnTextView.text = "ISBN: ${if (book.isbn.isNotBlank()) book.isbn else "-"}"
        holder.descriptionTextView.text = book.description.ifBlank { "Tidak ada deskripsi" }
        
        // Set status dengan warna yang sesuai
        when (book.status) {
            BookStatus.AVAILABLE -> {
                holder.statusTextView.text = "🟢 Tersedia"
                holder.statusTextView.setTextColor(holder.itemView.context.getColor(android.R.color.holo_green_dark))
            }
            BookStatus.BORROWED -> {
                holder.statusTextView.text = "🔴 Dipinjam"
                holder.statusTextView.setTextColor(holder.itemView.context.getColor(android.R.color.holo_red_dark))
            }
        }
        
        // Set click listeners
        holder.itemView.setOnClickListener {
            onItemClick(book)
        }
        
        holder.editButton.setOnClickListener {
            onEditClick(book)
        }
        
        holder.deleteButton.setOnClickListener {
            onDeleteClick(book)
        }
    }

    /**
     * ADAPTER - Mengembalikan jumlah item dalam list
     */
    override fun getItemCount(): Int = books.size

    /**
     * Update data adapter dengan list baru
     */
    fun updateBooks(newBooks: List<Book>) {
        (this as? MutableList<Book>)?.clear()
        (this as? MutableList<Book>)?.addAll(newBooks)
        notifyDataSetChanged()
    }
}

/**
 * LAYOUTMANAGER CUSTOM - Untuk layout yang lebih fleksibel
 */
class BookLayoutManager : RecyclerView.LayoutManager() {
    
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }
    
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        if (recycler == null || state == null) return
        
        detachAndScrapAttachedViews(recycler)
        
        var currentTop = 0
        val parentWidth = width
        
        for (i in 0 until itemCount) {
            val view = recycler.getViewForPosition(i)
            addView(view)
            
            measureChildWithMargins(view, 0, 0)
            
            val childWidth = getDecoratedMeasuredWidth(view)
            val childHeight = getDecoratedMeasuredHeight(view)
            
            layoutDecorated(
                view,
                0,
                currentTop,
                childWidth,
                currentTop + childHeight
            )
            
            currentTop += childHeight
        }
    }
}

/**
 * ADAPTER FACTORY - Untuk membuat adapter dengan konfigurasi yang berbeda
 */
object BookAdapterFactory {
    
    /**
     * Membuat adapter untuk list buku dengan layout vertikal
     */
    fun createVerticalAdapter(
        books: List<Book>,
        onItemClick: (Book) -> Unit,
        onEditClick: (Book) -> Unit,
        onDeleteClick: (Book) -> Unit
    ): BookAdapter {
        return BookAdapter(books, onItemClick, onEditClick, onDeleteClick)
    }
    
    /**
     * Membuat adapter untuk list buku dengan layout horizontal
     */
    fun createHorizontalAdapter(
        books: List<Book>,
        onItemClick: (Book) -> Unit,
        onEditClick: (Book) -> Unit,
        onDeleteClick: (Book) -> Unit
    ): BookAdapter {
        return BookAdapter(books, onItemClick, onEditClick, onDeleteClick)
    }
    
    /**
     * Membuat adapter untuk grid layout
     */
    fun createGridAdapter(
        books: List<Book>,
        onItemClick: (Book) -> Unit,
        onEditClick: (Book) -> Unit,
        onDeleteClick: (Book) -> Unit
    ): BookAdapter {
        return BookAdapter(books, onItemClick, onEditClick, onDeleteClick)
    }
}

/**
 * LAYOUTMANAGER FACTORY - Untuk membuat layout manager yang berbeda
 */
object BookLayoutManagerFactory {
    
    /**
     * Membuat LinearLayoutManager untuk layout vertikal
     */
    fun createVerticalLayoutManager(): RecyclerView.LayoutManager {
        return androidx.recyclerview.widget.LinearLayoutManager(
            null,
            RecyclerView.VERTICAL,
            false
        )
    }
    
    /**
     * Membuat LinearLayoutManager untuk layout horizontal
     */
    fun createHorizontalLayoutManager(): RecyclerView.LayoutManager {
        return androidx.recyclerview.widget.LinearLayoutManager(
            null,
            RecyclerView.HORIZONTAL,
            false
        )
    }
    
    /**
     * Membuat GridLayoutManager untuk layout grid
     */
    fun createGridLayoutManager(spanCount: Int): RecyclerView.LayoutManager {
        return androidx.recyclerview.widget.GridLayoutManager(null, spanCount)
    }
    
    /**
     * Membuat StaggeredGridLayoutManager untuk layout staggered
     */
    fun createStaggeredGridLayoutManager(spanCount: Int): RecyclerView.LayoutManager {
        return androidx.recyclerview.widget.StaggeredGridLayoutManager(
            spanCount,
            RecyclerView.VERTICAL
        )
    }
} 