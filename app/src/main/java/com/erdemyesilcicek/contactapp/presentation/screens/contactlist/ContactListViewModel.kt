package com.erdemyesilcicek.contactapp.presentation.screens.contactlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erdemyesilcicek.contactapp.data.model.Contact
import com.erdemyesilcicek.contactapp.data.repository.ApiContactRepository
import com.erdemyesilcicek.contactapp.data.repository.DeviceContactsRepository
import com.erdemyesilcicek.contactapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
    private val repository: ApiContactRepository,
    private val deviceContactsRepository: DeviceContactsRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(ContactListState())
    val state: StateFlow<ContactListState> = _state.asStateFlow()
    
    init {
        loadContacts()
    }
    
    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        searchContacts(query)
    }
    
    private fun loadContacts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            // Önce cihaz rehberindeki tüm numaraları al
            val devicePhoneNumbers = deviceContactsRepository.getAllDevicePhoneNumbers()
            
            repository.getContacts().collect { contacts ->
                // Her contact için cihaz rehberinde olup olmadığını kontrol et
                val contactsWithDeviceStatus = contacts.map { contact ->
                    val normalizedPhone = contact.phoneNumber.filter { it.isDigit() || it == '+' }
                    contact.copy(isInDeviceContacts = devicePhoneNumbers.contains(normalizedPhone))
                }
                
                _state.update { 
                    it.copy(
                        contacts = contactsWithDeviceStatus,
                        groupedContacts = groupContactsByLetter(contactsWithDeviceStatus),
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun refreshContacts() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, error = null) }
            
            // Önce cihaz rehberindeki tüm numaraları al
            val devicePhoneNumbers = deviceContactsRepository.getAllDevicePhoneNumbers()
            
            when (val result = repository.refreshContacts()) {
                is NetworkResult.Success -> {
                    // Her contact için cihaz rehberinde olup olmadığını kontrol et
                    val contactsWithDeviceStatus = result.data.map { contact ->
                        val normalizedPhone = contact.phoneNumber.filter { it.isDigit() || it == '+' }
                        contact.copy(isInDeviceContacts = devicePhoneNumbers.contains(normalizedPhone))
                    }
                    
                    _state.update { 
                        it.copy(
                            contacts = contactsWithDeviceStatus,
                            groupedContacts = groupContactsByLetter(contactsWithDeviceStatus),
                            isRefreshing = false,
                            error = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _state.update { 
                        it.copy(
                            isRefreshing = false,
                            error = result.message
                        )
                    }
                }
                is NetworkResult.Loading -> {
                    // Already handled
                }
            }
        }
    }
    
    private fun searchContacts(query: String) {
        viewModelScope.launch {
            // Mevcut cihaz rehberi durumlarını sakla
            val currentDeviceContactsMap = _state.value.contacts.associate { 
                it.phoneNumber.filter { c -> c.isDigit() || c == '+' } to it.isInDeviceContacts 
            }
            
            repository.searchContacts(query).collect { contacts ->
                // Arama sonuçlarına cihaz rehberi durumlarını ekle
                val contactsWithDeviceStatus = contacts.map { contact ->
                    val normalizedPhone = contact.phoneNumber.filter { it.isDigit() || it == '+' }
                    contact.copy(isInDeviceContacts = currentDeviceContactsMap[normalizedPhone] ?: false)
                }
                
                _state.update { 
                    it.copy(
                        contacts = contactsWithDeviceStatus,
                        groupedContacts = groupContactsByLetter(contactsWithDeviceStatus)
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
    
    /**
     * READ_CONTACTS izni verildiğinde çağrılır
     * Cihaz rehberi durumlarını güncellemek için contact'ları yeniden yükler
     */
    fun onContactsPermissionGranted() {
        viewModelScope.launch {
            val devicePhoneNumbers = deviceContactsRepository.getAllDevicePhoneNumbers()
            
            val contactsWithDeviceStatus = _state.value.contacts.map { contact ->
                val normalizedPhone = contact.phoneNumber.filter { it.isDigit() || it == '+' }
                contact.copy(isInDeviceContacts = devicePhoneNumbers.contains(normalizedPhone))
            }
            
            _state.update { 
                it.copy(
                    contacts = contactsWithDeviceStatus,
                    groupedContacts = groupContactsByLetter(contactsWithDeviceStatus)
                )
            }
        }
    }
    
    private fun groupContactsByLetter(contacts: List<Contact>): Map<Char, List<Contact>> {
        return contacts.groupBy { it.displayLetter }
            .toSortedMap()
    }
}
