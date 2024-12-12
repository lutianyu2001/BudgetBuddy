package com.cs407.budgetbuddy.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Adapter for the main ViewPager2 that handles navigation between primary screens
 * @param fragmentManager FragmentManager for handling fragment transactions
 * @param lifecycle Lifecycle to properly handle fragment lifecycle events
 * @param fragments List of fragments to display in the pager
 */
class MainPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val fragments: List<Fragment>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}