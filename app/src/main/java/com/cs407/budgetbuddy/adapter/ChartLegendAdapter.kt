package com.cs407.budgetbuddy.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cs407.budgetbuddy.databinding.ItemChartLegendBinding
import com.cs407.budgetbuddy.util.NumberFormatter

/**
 * Adapter for displaying chart legend items in a RecyclerView
 */
class ChartLegendAdapter : RecyclerView.Adapter<ChartLegendAdapter.LegendViewHolder>() {

    private var categories: List<String> = emptyList()
    private var amounts: List<Double> = emptyList()
    private var percentages: List<Double> = emptyList()

    // Predefined colors for the chart segments
    private val chartColors = listOf(
        "#3682BE", // Blue
        "#45A776", // Green
        "#F05326", // Orange
        "#EED777", // Yellow
        "#334F65", // Navy
        "#844BB3"  // Purple
    )

    fun setData(categories: List<String>, amounts: List<Double>, percentages: List<Double>) {
        this.categories = categories
        this.amounts = amounts
        this.percentages = percentages
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LegendViewHolder {
        val binding = ItemChartLegendBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LegendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LegendViewHolder, position: Int) {
        holder.bind(
            category = categories[position],
            amount = amounts[position],
            percentage = percentages[position],
            color = chartColors[position % chartColors.size],
            isFirstItem = position == 0
        )
    }

    override fun getItemCount(): Int = categories.size

    class LegendViewHolder(
        private val binding: ItemChartLegendBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            category: String,
            amount: Double,
            percentage: Double,
            color: String,
            isFirstItem: Boolean
        ) {
            binding.apply {
                // Show header only for first item
                llTitle.visibility = if (isFirstItem) {
                    ViewGroup.VISIBLE
                } else {
                    ViewGroup.GONE
                }

                viewColor.setBackgroundColor(Color.parseColor(color))
                tvCategory.text = category
                tvPercentage.text = String.format("%.1f%%", percentage)
                tvAmount.text = NumberFormatter.formatCurrency(amount)
            }
        }
    }
}