package com.cs407.budgetbuddy.ui.support

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs407.budgetbuddy.adapter.FAQAdapter
import com.cs407.budgetbuddy.databinding.ActivitySupportBinding
import com.google.android.material.snackbar.Snackbar

class SupportActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupportBinding
    private val viewModel: SupportViewModel by viewModels()
    private lateinit var faqAdapter: FAQAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        // Setup FAQ RecyclerView
        faqAdapter = FAQAdapter()
        binding.recyclerViewFaq.apply {
            layoutManager = LinearLayoutManager(this@SupportActivity)
            adapter = faqAdapter
            setHasFixedSize(true)
        }

        binding.apply {
            // Setup support buttons
            buttonContactSupport.setOnClickListener {
                sendSupportEmail(
                    subject = "BudgetBuddy Support Request",
                    body = "Please describe your issue:\n\n"
                )
            }

            buttonReportIssue.setOnClickListener {
                val deviceInfo = """
                    Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}
                    Android Version: ${android.os.Build.VERSION.RELEASE}
                    App Version: ${packageManager.getPackageInfo(packageName, 0).versionName}
                """.trimIndent()

                sendSupportEmail(
                    subject = "BudgetBuddy Issue Report",
                    body = """
                        $deviceInfo
                        
                        Issue Description:
                        
                    """.trimIndent()
                )
            }

            buttonBackToLogin.setOnClickListener {
                finish()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.faqItems.observe(this) { items ->
            faqAdapter.submitList(items)
        }

        viewModel.supportAction.observe(this) { action ->
            when (action) {
                is SupportAction.OpenEmail -> {
                    try {
                        startActivity(action.emailIntent)
                    } catch (e: Exception) {
                        showError("No email app found")
                    }
                }
                is SupportAction.ShowError -> {
                    showError(action.message)
                }
            }
        }
    }

    private fun sendSupportEmail(subject: String, body: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@budgetbuddy.com")
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }
            startActivity(intent)
        } catch (e: Exception) {
            showError("No email app found")
        }
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}