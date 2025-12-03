package com.erdemyesilcicek.contactapp.presentation.screens.contactdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erdemyesilcicek.contactapp.data.repository.ApiContactRepository
import com.erdemyesilcicek.contactapp.util.NetworkResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ContactDetailViewModel.Factory::class)
class ContactDetailViewModel @AssistedInject constructor(
    private val repository: ApiContactRepository,
    @Assisted private val contactId: String
) : ViewModel() {
    
    private val _state = MutableStateFlow(ContactDetailState())
    val state: StateFlow<ContactDetailState> = _state.asStateFlow()
    
    init {
        loadContact()
    }
    
    private fun loadContact() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            when (val result = repository.fetchContactById(contactId)) {
                is NetworkResult.Success -> {
                    _state.update { 
                        it.copy(
                            contact = result.data,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _state.update { 
                        it.copy(
                            isLoading = false,
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
    
    fun showDropdownMenu() {
        _state.update { it.copy(showDropdownMenu = true) }
    }
    
    fun hideDropdownMenu() {
        _state.update { it.copy(showDropdownMenu = false) }
    }
    
    fun showDeleteDialog() {
        _state.update { 
            it.copy(
                showDeleteDialog = true,
                showDropdownMenu = false
            )
        }
    }
    
    fun hideDeleteDialog() {
        _state.update { it.copy(showDeleteDialog = false) }
    }
    
    fun deleteContact() {
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true, error = null) }
            
            when (val result = repository.removeContact(contactId)) {
                is NetworkResult.Success -> {
                    _state.update { 
                        it.copy(
                            showDeleteDialog = false,
                            isDeleting = false,
                            isDeleted = true,
                            error = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _state.update { 
                        it.copy(
                            showDeleteDialog = false,
                            isDeleting = false,
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
    
    fun markAsSavedToPhone() {
        _state.update { 
            it.copy(
                isSavedToPhone = true,
                showSavedToPhoneMessage = true
            )
        }
    }
    
    fun hidePhoneMessage() {
        _state.update { it.copy(showSavedToPhoneMessage = false) }
    }
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
    
    fun retry() {
        loadContact()
    }
    
    @AssistedFactory
    interface Factory {
        fun create(contactId: String): ContactDetailViewModel
    }
}
