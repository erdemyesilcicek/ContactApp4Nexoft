package com.erdemyesilcicek.contactapp.presentation.screens.contactdetail

import com.erdemyesilcicek.contactapp.data.model.Contact

data class ContactDetailState(
    val contact: Contact? = null,
    val isLoading: Boolean = true,
    val isDeleting: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val showDropdownMenu: Boolean = false,
    val isDeleted: Boolean = false,
    val isSavedToPhone: Boolean = false,
    val showSavedToPhoneMessage: Boolean = false,
    val error: String? = null
)
