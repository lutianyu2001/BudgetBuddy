package com.cs407.budgetbuddy.util

import java.math.BigInteger
import java.security.MessageDigest

/**
 * Utility class for MD5 hash operations
 */
object MD5Util {
    /**
     * Generates MD5 hash for the given input string
     * @param input String to be hashed
     * @return MD5 hash string
     */
    fun generateMD5(input: String): String {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val messageDigest = md.digest(input.toByteArray())
            val no = BigInteger(1, messageDigest)
            var hashText = no.toString(16)
            while (hashText.length < 32) {
                hashText = "0$hashText"
            }
            hashText
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}