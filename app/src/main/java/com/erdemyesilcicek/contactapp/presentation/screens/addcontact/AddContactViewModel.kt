package com.erdemyesilcicek.contactapp.presentation.screens.addcontact

import android.net.Uri
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
class AddContactViewModel @Inject constructor(
    private val repository: ApiContactRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(AddContactState())
    val state: StateFlow<AddContactState> = _state.asStateFlow()
    
    fun onFirstNameChange(firstName: String) {
        _state.update { 
            it.copy(
                firstName = firstName,
                isValid = validateForm(firstName, it.lastName, it.phoneNumber),
                error = null
            )
        }
    }
    
    fun onLastNameChange(lastName: String) {
        _state.update { 
            it.copy(
                lastName = lastName,
                isValid = validateForm(it.firstName, lastName, it.phoneNumber),
                error = null
            )
        }
    }
    
    fun onPhoneNumberChange(phoneNumber: String) {
        _state.update { 
            it.copy(
                phoneNumber = phoneNumber,
                isValid = validateForm(it.firstName, it.lastName, phoneNumber),
                error = null
            )
        }
    }
    
    fun onPhotoSelected(uri: Uri?) {
        _state.update { it.copy(photoUri = uri, error = null) }
        // Upload image when selected
        uri?.let { uploadImage(it) }
    }
    
    private fun uploadImage(uri: Uri) {
        viewModelScope.launch {
            _state.update { it.copy(isUploadingImage = true, error = null) }
            
            when (val result = repository.uploadImage(uri)) {
                is NetworkResult.Success -> {
                    _state.update { 
                        it.copy(
                            isUploadingImage = false,
                            profileImageUrl = result.data
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _state.update { 
                        it.copy(
                            isUploadingImage = false,
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
    
    fun showPhotoPicker() {
        _state.update { it.copy(showPhotoPickerSheet = true) }
    }
    
    fun hidePhotoPicker() {
        _state.update { it.copy(showPhotoPickerSheet = false) }
    }
    
    fun saveContact() {
        val currentState = _state.value
        if (!currentState.isValid) return
        
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            
            val contact = Contact(
                firstName = currentState.firstName.trim(),
                lastName = currentState.lastName.trim(),
                phoneNumber = currentState.phoneNumber.trim(),
                photoUri = currentState.profileImageUrl?.let { Uri.parse(it) } 
                    ?: currentState.photoUri
            )
            
            when (val result = repository.createContact(contact)) {
                is NetworkResult.Success -> {
                    _state.update { 
                        it.copy(
                            isSaving = false,
                            isSaved = true,
                            error = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _state.update { 
                        it.copy(
                            isSaving = false,
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
    
    fun resetSavedState() {
        _state.update { it.copy(isSaved = false) }
    }
    
    fun resetState() {
        _state.value = AddContactState()
    }
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
    
    private fun validateForm(firstName: String, lastName: String, phoneNumber: String): Boolean {
        return firstName.isNotBlank() && phoneNumber.isNotBlank()
    }
}
