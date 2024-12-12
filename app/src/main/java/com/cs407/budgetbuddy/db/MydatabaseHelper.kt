package com.cs407.budgetbuddy.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * SQLite database helper class for BudgetBuddy application
 * Manages database creation and version management
 */
class DatabaseHelper private constructor(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "BudgetBuddy.db"
        private const val DATABASE_VERSION = 1

        // Table Names
        const val TABLE_USER = "users"
        const val TABLE_TRANSACTION = "transactions"

        // Common Column Names
        const val COLUMN_ID = "id"
        const val COLUMN_USER_ID = "user_id"

        // User Table Columns
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_BANK_ACCOUNT = "bank_account"
        const val COLUMN_BANK_PASSWORD = "bank_password"
        const val COLUMN_EMAIL = "email"

        // Transaction Table Columns
        const val COLUMN_ACCOUNT = "account"
        const val COLUMN_DETAILS = "details"
        const val COLUMN_AMOUNT = "amount"
        const val COLUMN_STORE = "store"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_DATE = "date"
        const val COLUMN_COMMENTS = "comments"
        const val COLUMN_TYPE = "type"

        // Create Table Statements
        private const val CREATE_USER_TABLE = """
            CREATE TABLE $TABLE_USER (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_EMAIL TEXT,
                $COLUMN_BANK_ACCOUNT TEXT,
                $COLUMN_BANK_PASSWORD TEXT
            )
        """

        private const val CREATE_TRANSACTION_TABLE = """
            CREATE TABLE $TABLE_TRANSACTION (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_ID INTEGER NOT NULL,
                $COLUMN_ACCOUNT TEXT,
                $COLUMN_DETAILS TEXT,
                $COLUMN_AMOUNT REAL NOT NULL,
                $COLUMN_STORE TEXT,
                $COLUMN_CATEGORY TEXT,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_COMMENTS TEXT,
                $COLUMN_TYPE INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USER($COLUMN_ID)
            )
        """

        @Volatile
        private var instance: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            return instance ?: synchronized(this) {
                instance ?: DatabaseHelper(context).also { instance = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_USER_TABLE)
        db.execSQL(CREATE_TRANSACTION_TABLE)

        // Create indices for faster queries
        db.execSQL("CREATE INDEX idx_transaction_user_id ON $TABLE_TRANSACTION($COLUMN_USER_ID)")
        db.execSQL("CREATE INDEX idx_transaction_date ON $TABLE_TRANSACTION($COLUMN_DATE)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // In a production app, we would handle database migrations here
        // For now, we'll just drop and recreate
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTION")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }
}