package com.cs407.budgetbuddy.data.repository

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Base repository class providing common database operations
 */
abstract class Repository {
    protected suspend fun <T> executeQuery(
        query: String,
        args: Array<String>? = null,
        transform: (Cursor) -> T
    ): T? = withContext(Dispatchers.IO) {
        var cursor: Cursor? = null
        try {
            cursor = getDatabase().rawQuery(query, args)
            if (cursor.moveToFirst()) {
                return@withContext transform(cursor)
            }
            return@withContext null
        } catch (e: Exception) {
            throw DatabaseException("Error executing query: ${e.message}")
        } finally {
            cursor?.close()
        }
    }

    protected suspend fun <T> executeWriteWithResult(operation: (SQLiteDatabase) -> T): T =
        withContext(Dispatchers.IO) {
            try {
                operation(getDatabase())
            } catch (e: Exception) {
                throw DatabaseException("Error executing write operation: ${e.message}")
            }
        }

    protected suspend fun executeWrite(operation: (SQLiteDatabase) -> Unit) =
        withContext(Dispatchers.IO) {
            try {
                operation(getDatabase())
            } catch (e: Exception) {
                throw DatabaseException("Error executing write operation: ${e.message}")
            }
        }

    protected abstract fun getDatabase(): SQLiteDatabase
}

class DatabaseException(message: String) : Exception(message)