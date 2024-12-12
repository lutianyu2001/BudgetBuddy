package com.cs407.budgetbuddy.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.NumberFormat
import java.util.Locale

/**
 * TextWatcher for formatting currency input in real-time
 */
class CurrencyTextWatcher(
    private val editText: EditText
) : TextWatcher {
    private var current = ""
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Not used
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Not used
    }

    override fun afterTextChanged(s: Editable?) {
        if (s.toString() != current) {
            editText.removeTextChangedListener(this)

            val cleanString = s.toString().replace("\\D".toRegex(), "")
            
            val parsed = try {
                cleanString.toDouble() / 100
            } catch (e: NumberFormatException) {
                0.0
            }

            val formatted = currencyFormat.format(parsed)
            current = formatted
            editText.setText(formatted)
            editText.setSelection(formatted.length)

            editText.addTextChangedListener(this)
        }
    }
}