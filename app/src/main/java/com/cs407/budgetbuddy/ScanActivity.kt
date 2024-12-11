package com.cs407.budgetbuddy

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cs407.budgetbuddy.data.Transaction
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ScanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan) // Use the corresponding layout for this activity

        // Assume you have a button to trigger file selection
        val filePickerButton = findViewById<Button>(R.id.filePickerButton)
        filePickerButton.setOnClickListener {
            // Start an Intent to pick a file
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), PDF_PICK_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PDF_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val pdfUri: Uri? = data?.data
            if (pdfUri != null) {
                val pdfFile = copyUriToFile(pdfUri)
                if (pdfFile != null) {
                    val transactions = extractTransactionsFromPDF(pdfFile.absolutePath)

                    val displayText = transactions.joinToString("\n") { transaction ->
                        "Name: ${transaction.name}, Amount: ${transaction.amount}, Date: ${transaction.date}, Category: ${transaction.category}, Merchant: ${transaction.merchant}"
                    }

                    showToast(displayText)
                }
            }
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyUriToFile(uri: Uri): File? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("selected_pdf", ".pdf", cacheDir)
            val outputStream = FileOutputStream(tempFile)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun extractTransactionsFromPDF(pdfFilePath: String): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val document = PDDocument.load(File(pdfFilePath))
        val pdfText = PDFTextStripper().getText(document)
        document.close()

        val lines = pdfText.lines()

        var isParsingTable = false
        var currentTransaction: MutableMap<String, String> = mutableMapOf()
        var accumulatedMerchant = StringBuilder()
        val name = "Sanjana Golla" // Static for now

        for (line in lines) {
            if (line.contains("Trans. Date", ignoreCase = true) && line.contains("Purchases", ignoreCase = true)) {
                isParsingTable = true
                continue
            }

            if (isParsingTable) {
                val transactionRegex = """(\d{2}/\d{2})\s+([A-Z\s]+)\s+([a-zA-Z\s]+)\s+\$(-?\d+\.\d{2})""".toRegex()
                val matchResult = transactionRegex.find(line)

                if (matchResult != null) {
                    if (currentTransaction.isNotEmpty()) {
                        transactions.add(
                            Transaction(
                                name = name,
                                amount = currentTransaction["amount"]?.toDouble() ?: 0.0,
                                date = currentTransaction["date"] ?: "",
                                category = currentTransaction["category"] ?: "",
                                merchant = accumulatedMerchant.toString().trim()
                            )
                        )

                        currentTransaction.clear()
                        accumulatedMerchant.clear()
                    }
                    val (date, merchant, category, amount) = matchResult.destructured
                    currentTransaction["date"] = date
                    accumulatedMerchant.append(merchant.trim())
                    currentTransaction["category"] = category.trim()
                    currentTransaction["amount"] = amount.replace("[$,]".toRegex(), "").trim()
                } else {
                    if (currentTransaction.isNotEmpty()) {
                        accumulatedMerchant.append(" ").append(line.trim())
                    }
                }
            }

            if (currentTransaction.isNotEmpty()) {
                transactions.add(
                    Transaction(
                        name = name,
                        amount = currentTransaction["amount"]?.toDouble() ?: 0.0,
                        date = currentTransaction["date"] ?: "",
                        category = currentTransaction["category"] ?: "",
                        merchant = accumulatedMerchant.toString().trim()
                    )
                )
            }
        }


        return transactions
    }

    companion object {
        private const val PDF_PICK_REQUEST_CODE = 1001
    }
}