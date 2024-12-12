package com.cs407.budgetbuddy.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cs407.budgetbuddy.R
import com.cs407.budgetbuddy.databinding.ItemTransactionBinding
import com.cs407.budgetbuddy.model.Transaction
import com.cs407.budgetbuddy.util.RecyclerViewListener

/**
 * Adapter for displaying transaction items in a RecyclerView
 */
class TransactionAdapter(
    private val listener: RecyclerViewListener? = null
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener?.onItemClick(it, absoluteAdapterPosition)
            }
            binding.root.setOnLongClickListener {
                listener?.onItemLongClick(it, absoluteAdapterPosition)
                true
            }
        }

        @SuppressLint("ResourceAsColor")
        fun bind(transaction: Transaction) {
            binding.apply {
                tvDate.text = transaction.getFormattedDate()
                tvCategory.text = transaction.category
                tvAmount.text = transaction.getFormattedAmount()
                
                // Set text color based on transaction type
                tvAmount.setTextColor(if (transaction.type.value == 1) {
                    R.color.income // Green for income
                } else {
                    R.color.expense // Pink for expense
                })
            }
        }
    }

    private class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}