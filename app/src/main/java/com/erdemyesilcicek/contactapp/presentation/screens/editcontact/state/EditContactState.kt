package com.erdemyesilcicek.contactapp.presentation.screens.editcontact

import android.net.Uri

data class EditContactState(
    val contactId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val photoUri: Uri? = null,
    val profileImageUrl: String? = null,
    val isLoading: Boolean = true,
    val isValid: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val isUploadingImage: Boolean = false,
    val showPhotoPickerSheet: Boolean = false,
    val error: String? = null
)
