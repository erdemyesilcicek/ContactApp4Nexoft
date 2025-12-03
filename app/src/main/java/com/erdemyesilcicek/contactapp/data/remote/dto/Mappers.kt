package com.erdemyesilcicek.contactapp.data.remote.dto

import android.net.Uri
import com.erdemyesilcicek.contactapp.data.model.Contact

/**
 * Extension functions for mapping between DTOs and domain models
 */

fun UserDto.toContact(): Contact {
    return Contact(
        id = id,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        photoUri = profileImageUrl?.let { Uri.parse(it) }
    )
}

fun Contact.toUserRequest(): UserRequest {
    return UserRequest(
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        profileImageUrl = photoUri?.toString()
    )
}
