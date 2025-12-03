package com.erdemyesilcicek.contactapp.data.repository

import com.erdemyesilcicek.contactapp.data.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    fun getContacts(): Flow<List<Contact>>
    fun searchContacts(query: String): Flow<List<Contact>>
    fun getContactById(contactId: String): Flow<Contact?>
    suspend fun addContact(contact: Contact)
    suspend fun deleteContact(contactId: String)
    suspend fun updateContact(contact: Contact)
}
