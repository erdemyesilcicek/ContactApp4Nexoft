package com.erdemyesilcicek.contactapp.presentation.screens.addcontact

import android.net.Uri

data class AddContactState(
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val photoUri: Uri? = null,
    val profileImageUrl: String? = null,
    val isValid: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val isUploadingImage: Boolean = false,
    val showPhotoPickerSheet: Boolean = false,
    val error: String? = null
)
