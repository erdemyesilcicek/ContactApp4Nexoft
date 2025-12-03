package com.erdemyesilcicek.contactapp.util

/**
 * Utility object for form validation operations
 */
object ValidationUtils {
    
    /**
     * Validates contact form fields
     * @param firstName First name of the contact
     * @param lastName Last name of the contact (optional)
     * @param phoneNumber Phone number of the contact
     * @return true if the form is valid, false otherwise
     */
    fun validateContactForm(
        firstName: String,
        lastName: String,
        phoneNumber: String
    ): Boolean {
        return firstName.isNotBlank() && phoneNumber.isNotBlank()
    }
    
    /**
     * Validates if a phone number has minimum required length
     * @param phoneNumber Phone number to validate
     * @param minLength Minimum required length (default: 7)
     * @return true if valid, false otherwise
     */
    fun isValidPhoneNumber(phoneNumber: String, minLength: Int = 7): Boolean {
        val digitsOnly = phoneNumber.filter { it.isDigit() }
        return digitsOnly.length >= minLength
    }
    
    /**
     * Validates if a name contains only valid characters
     * @param name Name to validate
     * @return true if valid, false otherwise
     */
    fun isValidName(name: String): Boolean {
        if (name.isBlank()) return false
        return name.all { it.isLetter() || it.isWhitespace() || it == '-' || it == '\'' }
    }
}
