package com.erdemyesilcicek.contactapp.data.repository

import com.erdemyesilcicek.contactapp.data.model.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaticContactRepository @Inject constructor() : ContactRepository {
    
    private val contacts = MutableStateFlow<List<Contact>>(emptyList())
    
    override fun getContacts(): Flow<List<Contact>> {
        return contacts.map { contactList ->
            contactList.sortedBy { it.firstName.lowercase() }
        }
    }
    
    override fun searchContacts(query: String): Flow<List<Contact>> {
        return contacts.map { contactList ->
            if (query.isBlank()) {
                contactList.sortedBy { it.firstName.lowercase() }
            } else {
                contactList.filter { contact ->
                    contact.fullName.contains(query, ignoreCase = true) ||
                    contact.phoneNumber.contains(query)
                }.sortedBy { it.firstName.lowercase() }
            }
        }
    }
    
    override fun getContactById(contactId: String): Flow<Contact?> {
        return contacts.map { contactList ->
            contactList.find { it.id == contactId }
        }
    }
    
    override suspend fun addContact(contact: Contact) {
        contacts.value = contacts.value + contact
    }
    
    override suspend fun deleteContact(contactId: String) {
        contacts.value = contacts.value.filter { it.id != contactId }
    }
    
    override suspend fun updateContact(contact: Contact) {
        contacts.value = contacts.value.map { 
            if (it.id == contact.id) contact else it 
        }
    }
}
