package com.cs407.budgetbuddy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cs407.budgetbuddy.R
import com.cs407.budgetbuddy.databinding.ItemFaqBinding
import com.cs407.budgetbuddy.model.FAQItem

class FAQAdapter : ListAdapter<FAQItem, FAQAdapter.FAQViewHolder>(FAQDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val binding = ItemFaqBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FAQViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FAQViewHolder(
        private val binding: ItemFaqBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                binding.textViewAnswer.visibility = if (binding.textViewAnswer.visibility == ViewGroup.VISIBLE) {
                    binding.imageExpand.setImageResource(R.drawable.ic_expand_more)
                    ViewGroup.GONE
                } else {
                    binding.imageExpand.setImageResource(R.drawable.ic_expand_less)
                    ViewGroup.VISIBLE
                }
            }
        }

        fun bind(item: FAQItem) {
            binding.apply {
                textViewQuestion.text = item.question
                textViewAnswer.text = item.answer
                textViewAnswer.visibility = ViewGroup.GONE
                imageExpand.setImageResource(R.drawable.ic_expand_more)
            }
        }
    }

    private class FAQDiffCallback : DiffUtil.ItemCallback<FAQItem>() {
        override fun areItemsTheSame(oldItem: FAQItem, newItem: FAQItem): Boolean {
            return oldItem.question == newItem.question
        }

        override fun areContentsTheSame(oldItem: FAQItem, newItem: FAQItem): Boolean {
            return oldItem == newItem
        }
    }
}