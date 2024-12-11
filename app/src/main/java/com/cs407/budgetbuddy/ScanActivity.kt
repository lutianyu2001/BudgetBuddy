package com.cs407.budgetbuddy

import android.app.Activity
import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cs407.budgetbuddy.data.Transaction
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                    CoroutineScope(Dispatchers.IO).launch {
                        val transactions = extractTransactionsFromPDF(pdfFile.absolutePath)

                        val displayText = transactions.joinToString("\n") { transaction ->
                            "Name: ${transaction.name}, Amount: ${transaction.amount}, Date: ${transaction.date}, Category: ${transaction.category}, Merchant: ${transaction.merchant}"
                        }

                        withContext(Dispatchers.Main) {
                            showToast(displayText)
                            Log.d("ScanActivity", "Extracted Transactions:\n$displayText")
                        }
                    }
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

            Log.d("ScanActivity", "PDF File path: ${tempFile.absolutePath}")


            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun extractTransactionsFromPDF(filePath: String): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val name = "Sanjana Golla"

        try {
            val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(PdfReader(filePath))

            for (page in 1..pdfDocument.numberOfPages) {
                val pageText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(page))
                val lines = pageText.lines()

                var currentTransaction: MutableMap<String, String> = mutableMapOf()
                val accumulatedMerchant = StringBuilder()

                val transactionRegex = """^(\d{2}/\d{2})\s+(.*)\s+([a-zA-Z\s]+)\s+\$(-?\d+\.\d{2})$""".toRegex()

                for (line in lines) {
                    val matchResult = transactionRegex.matchEntire(line)

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
                        currentTransaction["category"] = category
                        currentTransaction["amount"] = amount.replace("[$,]".toRegex(), "").trim()
                        accumulatedMerchant.append(merchant.trim())
                    } else if (currentTransaction.isNotEmpty()) {
                        accumulatedMerchant.append(" ").append(line.trim())
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

            pdfDocument.close()
        } catch (e: Exception) {
            Log.e("ScanActivity", "Error extracting transactions", e)
        }

        return transactions
    }

    companion object {
        private const val PDF_PICK_REQUEST_CODE = 1001
    }
}