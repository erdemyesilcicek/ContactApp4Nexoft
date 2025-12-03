package com.erdemyesilcicek.contactapp.presentation.screens.contactlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erdemyesilcicek.contactapp.data.model.Contact
import com.erdemyesilcicek.contactapp.data.repository.ApiContactRepository
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
    private val repository: ApiContactRepository
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
            
            repository.getContacts().collect { contacts ->
                _state.update { 
                    it.copy(
                        contacts = contacts,
                        groupedContacts = groupContactsByLetter(contacts),
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun refreshContacts() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, error = null) }
            
            when (val result = repository.refreshContacts()) {
                is NetworkResult.Success -> {
                    _state.update { 
                        it.copy(
                            contacts = result.data,
                            groupedContacts = groupContactsByLetter(result.data),
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
            repository.searchContacts(query).collect { contacts ->
                _state.update { 
                    it.copy(
                        contacts = contacts,
                        groupedContacts = groupContactsByLetter(contacts)
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
    
    private fun groupContactsByLetter(contacts: List<Contact>): Map<Char, List<Contact>> {
        return contacts.groupBy { it.displayLetter }
            .toSortedMap()
    }
}
