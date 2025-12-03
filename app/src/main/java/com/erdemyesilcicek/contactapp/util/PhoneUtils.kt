package com.erdemyesilcicek.contactapp.util

/**
 * Utility object for phone number operations
 */
object PhoneUtils {
    
    /**
     * Normalizes a phone number by removing all non-digit characters except '+'
     * @param phoneNumber Phone number to normalize
     * @return Normalized phone number containing only digits and optionally '+'
     */
    fun normalizePhoneNumber(phoneNumber: String): String {
        return phoneNumber.filter { it.isDigit() || it == '+' }
    }
    
    /**
     * Formats a phone number for display
     * @param phoneNumber Phone number to format
     * @return Formatted phone number
     */
    fun formatPhoneNumber(phoneNumber: String): String {
        val normalized = normalizePhoneNumber(phoneNumber)
        return when {
            normalized.startsWith("+90") && normalized.length == 13 -> {
                // Turkish format: +90 5XX XXX XX XX
                buildString {
                    append(normalized.substring(0, 3))
                    append(" ")
                    append(normalized.substring(3, 6))
                    append(" ")
                    append(normalized.substring(6, 9))
                    append(" ")
                    append(normalized.substring(9, 11))
                    append(" ")
                    append(normalized.substring(11, 13))
                }
            }
            normalized.length == 10 -> {
                // Format: XXX XXX XX XX
                buildString {
                    append(normalized.substring(0, 3))
                    append(" ")
                    append(normalized.substring(3, 6))
                    append(" ")
                    append(normalized.substring(6, 8))
                    append(" ")
                    append(normalized.substring(8, 10))
                }
            }
            else -> phoneNumber
        }
    }
    
    /**
     * Checks if two phone numbers are equal after normalization
     * @param phone1 First phone number
     * @param phone2 Second phone number
     * @return true if numbers are equal, false otherwise
     */
    fun arePhoneNumbersEqual(phone1: String, phone2: String): Boolean {
        return normalizePhoneNumber(phone1) == normalizePhoneNumber(phone2)
    }
    
    /**
     * Extracts country code from a phone number
     * @param phoneNumber Phone number with country code
     * @return Country code or null if not found
     */
    fun extractCountryCode(phoneNumber: String): String? {
        val normalized = normalizePhoneNumber(phoneNumber)
        return when {
            normalized.startsWith("+90") -> "+90"
            normalized.startsWith("+1") -> "+1"
            normalized.startsWith("+44") -> "+44"
            normalized.startsWith("+49") -> "+49"
            normalized.startsWith("+") -> normalized.take(3)
            else -> null
        }
    }
}
