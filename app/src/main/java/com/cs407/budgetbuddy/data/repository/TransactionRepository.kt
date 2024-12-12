package com.cs407.budgetbuddy.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.cs407.budgetbuddy.data.PreferencesManager
import com.cs407.budgetbuddy.db.DatabaseHelper
import com.cs407.budgetbuddy.model.Transaction
import com.cs407.budgetbuddy.model.TransactionType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Repository for handling transaction-related database operations
 */
class TransactionRepository private constructor(context: Context) : Repository() {
    private val dbHelper = DatabaseHelper.getInstance(context)
    private val prefsManager = PreferencesManager.getInstance(context)
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun getDatabase(): SQLiteDatabase = dbHelper.writableDatabase

    /**
     * Gets a specific transaction by ID
     */
    suspend fun getTransaction(transactionId: Long): Transaction? {
        return executeQuery(
            """
            SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION}
            WHERE ${DatabaseHelper.COLUMN_ID} = ?
            """,
            arrayOf(transactionId.toString())
        ) { cursor ->
            if (cursor.moveToFirst()) {
                cursor.toTransaction()
            } else {
                null
            }
        }
    }

    /**
     * Gets all transactions for the current user
     */
    suspend fun getAllTransactions(): List<Transaction> {
        val userId = prefsManager.getCurrentUserId() ?: return emptyList()
        return executeQuery(
            """
            SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION}
            WHERE ${DatabaseHelper.COLUMN_USER_ID} = ?
            ORDER BY ${DatabaseHelper.COLUMN_DATE} DESC
            """,
            arrayOf(userId.toString())
        ) { cursor ->
            val transactions = mutableListOf<Transaction>()
            do {
                transactions.add(cursor.toTransaction())
            } while (cursor.moveToNext())
            transactions
        } ?: emptyList()
    }

    /**
     * Gets daily expense totals for a date range
     */
    suspend fun getDailyExpenseTotals(startDate: LocalDate, endDate: LocalDate): Map<LocalDate, Double> {
        val userId = prefsManager.getCurrentUserId() ?: return emptyMap()

        return executeQuery(
            """
            SELECT DATE(${DatabaseHelper.COLUMN_DATE}) as date,
                   SUM(${DatabaseHelper.COLUMN_AMOUNT}) as total
            FROM ${DatabaseHelper.TABLE_TRANSACTION}  
            WHERE ${DatabaseHelper.COLUMN_USER_ID} = ?
            AND ${DatabaseHelper.COLUMN_TYPE} = ?
            AND DATE(${DatabaseHelper.COLUMN_DATE}) BETWEEN ? AND ?
            GROUP BY DATE(${DatabaseHelper.COLUMN_DATE})
            """,
            arrayOf(
                userId.toString(),
                TransactionType.EXPENSE.value.toString(),
                startDate.toString(),
                endDate.toString()
            )
        ) { cursor ->
            val dailyTotals = mutableMapOf<LocalDate, Double>()
            do {
                val date = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow("date")))
                val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
                dailyTotals[date] = total
            } while (cursor.moveToNext())
            dailyTotals
        } ?: emptyMap()
    }

    /**
     * Gets transactions by category
     */
    suspend fun getTransactionsByCategory(category: String): List<Transaction> {
        val userId = prefsManager.getCurrentUserId() ?: return emptyList()
        return executeQuery(
            """
            SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION}
            WHERE ${DatabaseHelper.COLUMN_USER_ID} = ?
            AND ${DatabaseHelper.COLUMN_CATEGORY} = ?
            ORDER BY ${DatabaseHelper.COLUMN_DATE} DESC
            """,
            arrayOf(userId.toString(), category)
        ) { cursor ->
            val transactions = mutableListOf<Transaction>()
            do {
                transactions.add(cursor.toTransaction())
            } while (cursor.moveToNext())
            transactions
        } ?: emptyList()
    }

    /**
     * Gets transactions for a specific date range
     */
    suspend fun getTransactionsForDateRange(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        val userId = prefsManager.getCurrentUserId() ?: return emptyList()
        return executeQuery(
            """
            SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION}
            WHERE ${DatabaseHelper.COLUMN_USER_ID} = ?
            AND ${DatabaseHelper.COLUMN_DATE} BETWEEN ? AND ?
            ORDER BY ${DatabaseHelper.COLUMN_DATE} DESC
            """,
            arrayOf(
                userId.toString(),
                dateFormatter.format(startDate),
                dateFormatter.format(endDate)
            )
        ) { cursor ->
            val transactions = mutableListOf<Transaction>()
            do {
                transactions.add(cursor.toTransaction())
            } while (cursor.moveToNext())
            transactions
        } ?: emptyList()
    }

    /**
     * Gets transactions for a specific month
     */
    suspend fun getTransactionsForMonth(year: Int, month: Int): List<Transaction> {
        val userId = prefsManager.getCurrentUserId() ?: return emptyList()
        return executeQuery(
            """
            SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION}
            WHERE ${DatabaseHelper.COLUMN_USER_ID} = ?
            AND strftime('%Y', ${DatabaseHelper.COLUMN_DATE}) = ?
            AND strftime('%m', ${DatabaseHelper.COLUMN_DATE}) = ?
            ORDER BY ${DatabaseHelper.COLUMN_DATE} DESC
            """,
            arrayOf(
                userId.toString(),
                year.toString(),
                String.format("%02d", month)
            )
        ) { cursor ->
            val transactions = mutableListOf<Transaction>()
            do {
                transactions.add(cursor.toTransaction())
            } while (cursor.moveToNext())
            transactions
        } ?: emptyList()
    }

    /**
     * Adds a new transaction
     */
    suspend fun addTransaction(transaction: Transaction): Long {
        val userId = prefsManager.getCurrentUserId() ?: throw IllegalStateException("User not logged in")

        return executeWriteWithResult { db ->
            db.beginTransaction()
            try {
                val values = ContentValues().apply {
                    put(DatabaseHelper.COLUMN_USER_ID, userId)
                    put(DatabaseHelper.COLUMN_ACCOUNT, transaction.account)
                    put(DatabaseHelper.COLUMN_DETAILS, transaction.details)
                    put(DatabaseHelper.COLUMN_AMOUNT, transaction.amount)
                    put(DatabaseHelper.COLUMN_STORE, transaction.store)
                    put(DatabaseHelper.COLUMN_CATEGORY, transaction.category)
                    put(DatabaseHelper.COLUMN_DATE, dateFormatter.format(transaction.date))
                    put(DatabaseHelper.COLUMN_COMMENTS, transaction.comments)
                    put(DatabaseHelper.COLUMN_TYPE, transaction.type.value)
                }

                val newId = db.insertOrThrow(DatabaseHelper.TABLE_TRANSACTION, null, values)
                if (newId == -1L) {
                    throw DatabaseException("Failed to insert transaction")
                }

                db.setTransactionSuccessful()
                newId
            } finally {
                db.endTransaction()
            }
        }
    }

    /**
     * Updates an existing transaction
     */
    suspend fun updateTransaction(transaction: Transaction): Boolean {
        return executeWriteWithResult { db ->
            db.beginTransaction()
            try {
                val values = ContentValues().apply {
                    put(DatabaseHelper.COLUMN_ACCOUNT, transaction.account)
                    put(DatabaseHelper.COLUMN_DETAILS, transaction.details)
                    put(DatabaseHelper.COLUMN_AMOUNT, transaction.amount)
                    put(DatabaseHelper.COLUMN_STORE, transaction.store)
                    put(DatabaseHelper.COLUMN_CATEGORY, transaction.category)
                    put(DatabaseHelper.COLUMN_DATE, dateFormatter.format(transaction.date))
                    put(DatabaseHelper.COLUMN_COMMENTS, transaction.comments)
                    put(DatabaseHelper.COLUMN_TYPE, transaction.type.value)
                }

                val rows = db.update(
                    DatabaseHelper.TABLE_TRANSACTION,
                    values,
                    "${DatabaseHelper.COLUMN_ID} = ?",
                    arrayOf(transaction.id.toString())
                )

                if (rows > 0) {
                    db.setTransactionSuccessful()
                    true
                } else {
                    false
                }
            } finally {
                db.endTransaction()
            }
        }
    }

    /**
     * Deletes a transaction
     */
    suspend fun deleteTransaction(transactionId: Long): Boolean {
        return executeWriteWithResult { db ->
            db.beginTransaction()
            try {
                val rows = db.delete(
                    DatabaseHelper.TABLE_TRANSACTION,
                    "${DatabaseHelper.COLUMN_ID} = ?",
                    arrayOf(transactionId.toString())
                )
                if (rows > 0) {
                    db.setTransactionSuccessful()
                    true
                } else {
                    false
                }
            } finally {
                db.endTransaction()
            }
        }
    }

    /**
     * Gets categories with their total amounts for a specific date range
     */
    suspend fun getCategoryTotals(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        type: TransactionType? = null
    ): Map<String, Double> {
        val userId = prefsManager.getCurrentUserId() ?: return emptyMap()

        val typeClause = type?.let {
            "AND ${DatabaseHelper.COLUMN_TYPE} = ?"
        } ?: ""

        val queryArgs = mutableListOf(
            userId.toString(),
            dateFormatter.format(startDate),
            dateFormatter.format(endDate)
        )
        if (type != null) {
            queryArgs.add(type.value.toString())
        }

        return executeQuery(
            """
            SELECT ${DatabaseHelper.COLUMN_CATEGORY},
                   SUM(${DatabaseHelper.COLUMN_AMOUNT}) as total
            FROM ${DatabaseHelper.TABLE_TRANSACTION}
            WHERE ${DatabaseHelper.COLUMN_USER_ID} = ?
            AND ${DatabaseHelper.COLUMN_DATE} BETWEEN ? AND ?
            $typeClause
            GROUP BY ${DatabaseHelper.COLUMN_CATEGORY}
            """,
            queryArgs.toTypedArray()
        ) { cursor ->
            val totals = mutableMapOf<String, Double>()
            do {
                val category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY))
                val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
                totals[category] = total
            } while (cursor.moveToNext())
            totals
        } ?: emptyMap()
    }

    private fun Cursor.toTransaction(): Transaction {
        return Transaction(
            id = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
            userId = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)),
            account = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACCOUNT)),
            details = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_DETAILS)),
            amount = getDouble(getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT)),
            store = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_STORE)),
            category = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY)),
            date = LocalDateTime.parse(
                getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE)),
                dateFormatter
            ),
            comments = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_COMMENTS)),
            type = TransactionType.fromInt(
                getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_TYPE))
            )
        )
    }

    companion object {
        @Volatile
        private var instance: TransactionRepository? = null

        fun getInstance(context: Context): TransactionRepository {
            return instance ?: synchronized(this) {
                instance ?: TransactionRepository(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
}