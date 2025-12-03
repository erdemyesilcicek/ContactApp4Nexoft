package com.erdemyesilcicek.contactapp.presentation.screens.contactlist

import com.erdemyesilcicek.contactapp.data.model.Contact

data class ContactListState(
    val contacts: List<Contact> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val groupedContacts: Map<Char, List<Contact>> = emptyMap(),
    val error: String? = null
)
