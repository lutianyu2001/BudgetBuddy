package com.cs407.budgetbuddy.data

data class Transaction(
    val name: String,
    val date: String,
    val amount: Double,
    val category: String,
    val merchant: String
)
