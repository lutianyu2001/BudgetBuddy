package com.cs407.budgetbuddy.ui.support

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cs407.budgetbuddy.model.FAQItem

class SupportViewModel : ViewModel() {
    private val _faqItems = MutableLiveData<List<FAQItem>>()
    val faqItems: LiveData<List<FAQItem>> = _faqItems

    private val _supportAction = MutableLiveData<SupportAction>()
    val supportAction: LiveData<SupportAction> = _supportAction

    init {
        loadFAQItems()
    }

    fun contactSupport() {
        createEmailIntent(
            subject = "BudgetBuddy Support Request",
            body = "Please describe your issue:\n\n"
        )
    }

    fun reportIssue() {
        createEmailIntent(
            subject = "BudgetBuddy Issue Report",
            body = """
                Please provide the following information:
                
                Device:
                Android Version:
                App Version:
                
                Issue Description:
                
            """.trimIndent()
        )
    }

    private fun createEmailIntent(subject: String, body: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("support@budgetbuddy.com"))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        _supportAction.value = SupportAction.OpenEmail(intent)
    }

    private fun loadFAQItems() {
        _faqItems.value = listOf(
            FAQItem(
                question = "How do I add a transaction?",
                answer = "Tap the + button on the home screen to add a new transaction."
            ),
            FAQItem(
                question = "How do I connect my bank account?",
                answer = "Go to Settings > Bank Account > Connect Account and follow the instructions."
            ),
            FAQItem(
                question = "How do I export my data?",
                answer = "Go to Settings > Data > Export and choose your preferred format."
            ),
            // Add more FAQ items as needed
        )
    }
}

sealed class SupportAction {
    data class OpenEmail(val emailIntent: Intent) : SupportAction()
    data class ShowError(val message: String) : SupportAction()
}