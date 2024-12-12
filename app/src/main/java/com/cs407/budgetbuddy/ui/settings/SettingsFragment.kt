package com.cs407.budgetbuddy.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cs407.budgetbuddy.R
import com.cs407.budgetbuddy.databinding.FragmentSettingsBinding
import com.cs407.budgetbuddy.ui.auth.LoginActivity
import com.cs407.budgetbuddy.ui.common.ViewModelFactory
import com.cs407.budgetbuddy.ui.profile.EditProfileActivity
import com.cs407.budgetbuddy.util.NumberFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.apply {
            cardProfile.setOnClickListener {
                startActivity(Intent(requireContext(), EditProfileActivity::class.java))
            }

            cardBankAccount.setOnClickListener {
                showBankAccountDialog()
            }

            cardSupport.setOnClickListener {
                showSupportOptions()
            }

            buttonLogout.setOnClickListener {
                showLogoutConfirmation()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            binding.apply {
                textUsername.text = profile.username
                textEmail.text = profile.email ?: "No email set"
                textBankAccount.text = if (profile.hasBankAccount) {
                    "Bank account connected"
                } else {
                    "No bank account connected"
                }
            }
        }

//        viewModel.financialSummary.observe(viewLifecycleOwner) { summary ->
//            binding.apply {
//                textExpenses.text = "- $${NumberFormatter.formatCurrency(summary.totalExpenses)}"
//                textIncome.text = "+ $${NumberFormatter.formatCurrency(summary.totalIncome)}"
//                textBalance.text = "$${NumberFormatter.formatCurrency(summary.balance)}"
//            }
//        }

        viewModel.events.observe(viewLifecycleOwner) { event ->
            when (event) {
                is SettingsEvent.NavigateToLogin -> {
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                }
                is SettingsEvent.ShowError -> {
                    showError(event.message)
                }
                is SettingsEvent.ShowSuccess -> {
                    showSuccess(event.message)
                }

                is SettingsEvent.OpenEmail -> TODO()
            }
        }
    }

    private fun showBankAccountDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Bank Account")
            .setItems(arrayOf("Connect Account", "Sync Transactions", "Remove Account")) { _, which ->
                when (which) {
                    0 -> viewModel.connectBankAccount()
                    1 -> viewModel.syncBankTransactions()
                    2 -> viewModel.removeBankAccount()
                }
            }
            .show()
    }

    private fun showSupportOptions() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Support")
            .setItems(arrayOf("FAQ", "Contact Support", "Report Issue")) { _, which ->
                when (which) {
                    0 -> viewModel.showFAQ()
                    1 -> viewModel.contactSupport()
                    2 -> viewModel.reportIssue()
                }
            }
            .show()
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ -> viewModel.logout() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(resources.getColor(R.color.holo_red_light, null))
            .show()
    }

    private fun showSuccess(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(resources.getColor(R.color.holo_green_light, null))
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}