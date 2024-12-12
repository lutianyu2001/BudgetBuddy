package com.cs407.budgetbuddy.ui.transaction

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.cs407.budgetbuddy.R
import com.cs407.budgetbuddy.databinding.ActivityTransactionDetailBinding
import com.cs407.budgetbuddy.model.Transaction
import com.cs407.budgetbuddy.model.TransactionType
import com.cs407.budgetbuddy.util.CurrencyTextWatcher
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import com.cs407.budgetbuddy.ui.common.ViewModelFactory

class TransactionDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityTransactionDetailBinding
    private val viewModel: TransactionDetailViewModel by viewModels {
        ViewModelFactory(applicationContext)
    }
    
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get transaction ID from intent
        val transactionId = intent.getLongExtra(EXTRA_TRANSACTION_ID, -1)
        if (transactionId == -1L) {
            finish()
            return
        }

        setupToolbar()
        setupViews()
        observeViewModel()
        
        viewModel.loadTransaction(transactionId)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.transaction_details)
    }
    private fun showDatePicker() {
        val currentDate = viewModel.transaction.value?.date ?: LocalDateTime.now()
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(currentDate.toInstant(ZoneOffset.UTC).toEpochMilli())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val date = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(selection),
                ZoneId.systemDefault()
            )
            viewModel.setSelectedDate(date)
        }

        datePicker.show(supportFragmentManager, "datePicker")
    }

    private fun showCategoryPicker() {
        CategoryPickerDialog().show(supportFragmentManager, "categoryPicker")
    }

    private fun setupViews() {
        binding.apply {
            // Date picker
            layoutDate.setOnClickListener {
                if (isEditMode) showDatePicker()
            }

            // Category picker
            layoutCategory.setOnClickListener {
                if (isEditMode) showCategoryPicker()
            }

            // Edit/Save button
            fabEdit.setOnClickListener {
                if (isEditMode) {
                    saveChanges()
                } else {
                    enableEditMode()
                }
            }

            // Amount input formatting
            editTextAmount.addTextChangedListener(CurrencyTextWatcher(editTextAmount))
        }
    }

    private fun observeViewModel() {
        viewModel.transaction.observe(this) { transaction ->
            updateViews(transaction)
        }

        viewModel.state.observe(this) { state ->
            when (state) {
                is TransactionDetailState.Loading -> {
                    setLoading(true)
                }
                is TransactionDetailState.Success -> {
                    setLoading(false)
                    if (state.message != null) {
                        showSuccess(state.message)
                    }
                    if (state.finish) finish()
                }
                is TransactionDetailState.Error -> {
                    setLoading(false)
                    showError(state.message)
                }
                TransactionDetailState.Initial -> {
                    setLoading(false)
                }
            }
        }
    }

    private fun updateViews(transaction: Transaction) {
        binding.apply {
            // Set values
            textViewDate.text = transaction.getFormattedDate()
            textViewCategory.text = transaction.category
            editTextAmount.setText(transaction.getFormattedAmount())
            editTextDetails.setText(transaction.details)
            editTextStore.setText(transaction.store)
            editTextComments.setText(transaction.comments)
            
            // Set colors based on transaction type
            val color = if (transaction.type == TransactionType.INCOME) {
                getColor(R.color.income)
            } else {
                getColor(R.color.expense)
            }
            editTextAmount.setTextColor(color)
        }
    }

    private fun enableEditMode() {
        isEditMode = true
        binding.apply {
            // Enable editing
            editTextAmount.isEnabled = true
            editTextDetails.isEnabled = true
            editTextStore.isEnabled = true
            editTextComments.isEnabled = true
            
            // Update FAB
            fabEdit.setImageResource(R.drawable.ic_save)
            
            // Show delete button in toolbar
            invalidateOptionsMenu()
        }
    }

    private fun disableEditMode() {
        isEditMode = false
        binding.apply {
            // Disable editing
            editTextAmount.isEnabled = false
            editTextDetails.isEnabled = false
            editTextStore.isEnabled = false
            editTextComments.isEnabled = false
            
            // Update FAB
            fabEdit.setImageResource(R.drawable.ic_edit)
            
            // Hide delete button
            invalidateOptionsMenu()
        }
    }

    private fun saveChanges() {
        binding.apply {
            val amount = editTextAmount.text.toString().replace("[^0-9.]".toRegex(), "").toDouble()
            val details = editTextDetails.text.toString()
            val store = editTextStore.text.toString()
            val comments = editTextComments.text.toString()

            viewModel.updateTransaction(
                amount = amount,
                details = details,
                store = store,
                comments = comments
            )
        }
    }

    private fun showDeleteConfirmation() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_transaction)
            .setMessage(R.string.delete_transaction_confirmation)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteTransaction()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun setLoading(isLoading: Boolean) {
        binding.apply {
            progressBar.isVisible = isLoading
            fabEdit.isEnabled = !isLoading
        }
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(R.color.error))
            .show()
    }

    private fun showSuccess(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(getColor(R.color.success))
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (isEditMode) {
            menuInflater.inflate(R.menu.menu_transaction_detail, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_delete -> {
                showDeleteConfirmation()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (isEditMode) {
            disableEditMode()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        const val EXTRA_TRANSACTION_ID = "extra_transaction_id"
    }
}