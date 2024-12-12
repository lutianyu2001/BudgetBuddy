package com.cs407.budgetbuddy.ui.transaction

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cs407.budgetbuddy.databinding.ActivityAddTransactionBinding
import com.cs407.budgetbuddy.model.Transaction
import com.cs407.budgetbuddy.model.TransactionType
import com.cs407.budgetbuddy.ui.common.ViewModelFactory
import com.cs407.budgetbuddy.util.CurrencyTextWatcher
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class AddTransactionActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAddTransactionBinding
    private val viewModel: AddTransactionViewModel by viewModels {
        ViewModelFactory(applicationContext)
    }
    
    private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.apply {
            // Setup toolbar
            toolbar.setNavigationOnClickListener { finish() }
            
            // Setup amount input with currency formatting
            editTextAmount.addTextChangedListener(CurrencyTextWatcher(editTextAmount))

            // Setup date picker
            val today = LocalDateTime.now()
            textViewDate.text = today.format(dateFormatter)
            layoutDate.setOnClickListener {
                showDatePicker()
            }

            // Setup transaction type toggle
            toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    viewModel.setTransactionType(
                        if (checkedId == buttonExpense.id) {
                            TransactionType.EXPENSE
                        } else {
                            TransactionType.INCOME
                        }
                    )
                }
            }

            // Setup category selection
            layoutCategory.setOnClickListener {
                showCategoryPicker()
            }

            // Setup save button
            buttonSave.setOnClickListener {
                val transaction = Transaction(
                    account = editTextAccount.text.toString(),
                    amount = editTextAmount.text.toString().replace("[^0-9.]".toRegex(), "").toDouble(),
                    details = editTextDetails.text.toString(),
                    store = editTextStore.text.toString(),
                    category = textViewCategory.text.toString(),
                    date = viewModel.selectedDate.value ?: LocalDateTime.now(),
                    comments = editTextComments.text.toString(),
                    type = viewModel.transactionType.value ?: TransactionType.EXPENSE
                )
                viewModel.saveTransaction(transaction)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.selectedDate.observe(this) { date ->
            binding.textViewDate.text = date.format(dateFormatter)
        }

        viewModel.selectedCategory.observe(this) { category ->
            binding.textViewCategory.text = category
        }

        viewModel.saveState.observe(this) { state ->
            when (state) {
                is TransactionSaveState.Loading -> {
                    setLoading(true)
                }
                is TransactionSaveState.Success -> {
                    setLoading(false)
                    finish()
                }
                is TransactionSaveState.Error -> {
                    setLoading(false)
                    showError(state.message)
                }
                TransactionSaveState.Initial -> {
                    setLoading(false)
                }
            }
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
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

    private fun setLoading(isLoading: Boolean) {
        binding.buttonSave.isEnabled = !isLoading
        binding.progressBar.visibility = if (isLoading) {
            android.view.View.VISIBLE
        } else {
            android.view.View.GONE
        }
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}