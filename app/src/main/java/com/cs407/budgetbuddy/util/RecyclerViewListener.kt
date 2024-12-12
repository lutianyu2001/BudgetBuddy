package com.cs407.budgetbuddy.util

import android.view.View

/**
 * Interface for handling RecyclerView item clicks
 */
interface RecyclerViewListener {
    /**
     * Called when an item is clicked
     * @param view The clicked view
     * @param position Position of the clicked item
     */
    fun onItemClick(view: View, position: Int)

    /**
     * Called when an item is long clicked
     * @param view The long clicked view
     * @param position Position of the long clicked item
     */
    fun onItemLongClick(view: View, position: Int)
}