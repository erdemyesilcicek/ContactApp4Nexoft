package com.erdemyesilcicek.contactapp.presentation.screens.editcontact

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erdemyesilcicek.contactapp.data.model.Contact
import com.erdemyesilcicek.contactapp.data.repository.ApiContactRepository
import com.erdemyesilcicek.contactapp.util.NetworkResult
import com.erdemyesilcicek.contactapp.util.ValidationUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = EditContactViewModel.Factory::class)
class EditContactViewModel @AssistedInject constructor(
    private val repository: ApiContactRepository,
    @Assisted private val contactId: String
) : ViewModel() {
    
    private val _state = MutableStateFlow(EditContactState())
    val state: StateFlow<EditContactState> = _state.asStateFlow()
    
    init {
        loadContact()
    }
    
    private fun loadContact() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            when (val result = repository.fetchContactById(contactId)) {
                is NetworkResult.Success -> {
                    val contact = result.data
                    _state.update { state ->
                        state.copy(
                            contactId = contact.id,
                            firstName = contact.firstName,
                            lastName = contact.lastName,
                            phoneNumber = contact.phoneNumber,
                            photoUri = contact.photoUri,
                            profileImageUrl = contact.photoUri?.toString(),
                            isLoading = false,
                            isValid = ValidationUtils.validateContactForm(contact.firstName, contact.lastName, contact.phoneNumber),
                            error = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _state.update { state ->
                        state.copy(
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
    
    fun onFirstNameChange(firstName: String) {
        _state.update { 
            it.copy(
                firstName = firstName,
                isValid = ValidationUtils.validateContactForm(firstName, it.lastName, it.phoneNumber),
                error = null
            )
        }
    }
    
    fun onLastNameChange(lastName: String) {
        _state.update { 
            it.copy(
                lastName = lastName,
                isValid = ValidationUtils.validateContactForm(it.firstName, lastName, it.phoneNumber),
                error = null
            )
        }
    }
    
    fun onPhoneNumberChange(phoneNumber: String) {
        _state.update { 
            it.copy(
                phoneNumber = phoneNumber,
                isValid = ValidationUtils.validateContactForm(it.firstName, it.lastName, phoneNumber),
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
    
    fun updateContact() {
        val currentState = _state.value
        if (!currentState.isValid) return
        
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            
            val contact = Contact(
                id = currentState.contactId,
                firstName = currentState.firstName.trim(),
                lastName = currentState.lastName.trim(),
                phoneNumber = currentState.phoneNumber.trim(),
                photoUri = currentState.profileImageUrl?.let { Uri.parse(it) } 
                    ?: currentState.photoUri
            )
            
            when (val result = repository.modifyContact(contact)) {
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
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
    
    fun retry() {
        loadContact()
    }
    
    @AssistedFactory
    interface Factory {
        fun create(contactId: String): EditContactViewModel
    }
}
