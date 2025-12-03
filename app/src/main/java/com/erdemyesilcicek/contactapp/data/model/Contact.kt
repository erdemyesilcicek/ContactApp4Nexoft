package com.erdemyesilcicek.contactapp.data.model

import android.net.Uri
import java.util.UUID

data class Contact(
    val id: String = UUID.randomUUID().toString(),
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val photoUri: Uri? = null
) {
    val fullName: String
        get() = "$firstName $lastName".trim()
    
    val initials: String
        get() = buildString {
            if (firstName.isNotBlank()) append(firstName.first().uppercaseChar())
            if (lastName.isNotBlank()) append(lastName.first().uppercaseChar())
        }.ifBlank { "?" }
    
    val displayLetter: Char
        get() = firstName.firstOrNull()?.uppercaseChar() 
            ?: lastName.firstOrNull()?.uppercaseChar() 
            ?: '#'
}
