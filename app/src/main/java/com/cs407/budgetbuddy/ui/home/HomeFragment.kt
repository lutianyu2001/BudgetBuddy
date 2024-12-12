package com.cs407.budgetbuddy.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs407.budgetbuddy.adapter.TransactionAdapter
import com.cs407.budgetbuddy.databinding.FragmentHomeBinding
import com.cs407.budgetbuddy.model.Transaction
import com.cs407.budgetbuddy.util.RecyclerViewListener
import com.cs407.budgetbuddy.util.NumberFormatter
import com.cs407.budgetbuddy.ui.transaction.AddTransactionActivity
import com.cs407.budgetbuddy.ui.transaction.TransactionDetailActivity
import com.cs407.budgetbuddy.ui.common.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment(), RecyclerViewListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory(requireContext())
    }
    private val transactionAdapter = TransactionAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        // Setup RecyclerView
        binding.transactionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
            setHasFixedSize(true)
        }

        // Setup search with improved UX
        binding.editSearch.apply {
            // Set proper input type and IME options
            inputType = android.text.InputType.TYPE_CLASS_TEXT
            imeOptions = android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH

            // Add text change listener for real-time search
            addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    // Perform search after a short delay to avoid too frequent searches
                    removeCallbacks(searchRunnable)
                    postDelayed(searchRunnable, 300) // 300ms delay
                }
            })

            // Handle keyboard search action
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                    performSearch()
                    true
                } else {
                    false
                }
            }
        }

        // Setup add transaction button
        binding.imageAdd.setOnClickListener {
            startActivity(Intent(requireContext(), AddTransactionActivity::class.java))
        }

        // Setup swipe refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.editSearch.text?.clear()
            viewModel.refreshData()
        }
    }

    private val searchRunnable = Runnable {
        performSearch()
    }

    private fun performSearch() {
        val searchQuery = binding.editSearch.text.toString().trim()
        viewModel.searchTransactions(searchQuery)

        // Hide keyboard after search
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editSearch.windowToken, 0)
    }

    private fun observeViewModel() {
        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.submitList(transactions)
            updateEmptyState(transactions)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        viewModel.summary.observe(viewLifecycleOwner) { summary ->
            binding.apply {
                textExpenses.text = "- $${NumberFormatter.formatCurrency(summary.totalExpenses)}"
                textIncome.text = "+ $${NumberFormatter.formatCurrency(summary.totalIncome)}"
                textBalance.text = "$${NumberFormatter.formatCurrency(summary.balance)}"
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showError(it)
            }
        }
    }

    private fun updateEmptyState(transactions: List<Transaction>) {
        if (transactions.isEmpty() && !binding.swipeRefreshLayout.isRefreshing) {
            if (binding.editSearch.text!!.isNotEmpty()) {
                // Show no search results message
                showEmptyState("No transactions found matching your search")
            } else {
                // Show no transactions message
                showEmptyState("No transactions yet.\nTap + to add your first transaction")
            }
        } else {
            hideEmptyState()
        }
    }

    private fun showEmptyState(message: String) {
        binding.apply {
            emptyStateText.text = message
            emptyStateText.visibility = View.VISIBLE
            transactionsRecyclerView.visibility = View.GONE
        }
    }

    private fun hideEmptyState() {
        binding.apply {
            emptyStateText.visibility = View.GONE
            transactionsRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onItemClick(view: View, position: Int) {
        viewModel.getTransactionAt(position)?.let { transaction ->
            navigateToTransactionDetails(transaction)
        }
    }

    override fun onItemLongClick(view: View, position: Int) {
        // Not implemented - could be used for additional functionality
    }

    private fun navigateToTransactionDetails(transaction: Transaction) {
        val intent = Intent(requireContext(), TransactionDetailActivity::class.java).apply {
            putExtra(TransactionDetailActivity.EXTRA_TRANSACTION_ID, transaction.id)
        }
        startActivity(intent)
    }

    private fun showError(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        snackbar.setAction("Retry") {
            viewModel.refreshData()
        }
        snackbar.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}