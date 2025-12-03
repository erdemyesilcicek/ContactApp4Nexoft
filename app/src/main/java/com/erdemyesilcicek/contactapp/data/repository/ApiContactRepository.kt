package com.erdemyesilcicek.contactapp.data.repository

import android.content.Context
import android.net.Uri
import com.erdemyesilcicek.contactapp.data.model.Contact
import com.erdemyesilcicek.contactapp.data.remote.api.UserApi
import com.erdemyesilcicek.contactapp.data.remote.dto.UserRequest
import com.erdemyesilcicek.contactapp.data.remote.dto.toContact
import com.erdemyesilcicek.contactapp.util.NetworkResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiContactRepository @Inject constructor(
    private val userApi: UserApi,
    @ApplicationContext private val context: Context
) : ContactRepository {
    
    private val contactsCache = MutableStateFlow<List<Contact>>(emptyList())
    
    override fun getContacts(): Flow<List<Contact>> {
        return flow {
            emit(contactsCache.value) 
            
            when (val result = fetchAllContacts()) {
                is NetworkResult.Success -> {
                    contactsCache.value = result.data.sortedBy { it.firstName.lowercase() }
                    emit(contactsCache.value)
                }
                is NetworkResult.Error -> {
                    emit(contactsCache.value)
                }
                is NetworkResult.Loading -> {
                }
            }
        }.flowOn(Dispatchers.IO)
    }
    
    override fun searchContacts(query: String): Flow<List<Contact>> {
        return contactsCache.map { contacts ->
            if (query.isBlank()) {
                contacts.sortedBy { it.firstName.lowercase() }
            } else {
                contacts.filter { contact ->
                    contact.fullName.contains(query, ignoreCase = true) ||
                    contact.phoneNumber.contains(query)
                }.sortedBy { it.firstName.lowercase() }
            }
        }
    }
    
    override fun getContactById(contactId: String): Flow<Contact?> {
        return flow {
            val cachedContact = contactsCache.value.find { it.id == contactId }
            emit(cachedContact)
            
            when (val result = fetchContactById(contactId)) {
                is NetworkResult.Success -> {
                    emit(result.data)
                    updateCache(result.data)
                }
                is NetworkResult.Error -> {
                    emit(cachedContact)
                }
                is NetworkResult.Loading -> {
                }
            }
        }.flowOn(Dispatchers.IO)
    }
    
    override suspend fun addContact(contact: Contact) {
        withContext(Dispatchers.IO) {
            val result = createContact(contact)
            if (result is NetworkResult.Success) {
                contactsCache.value = contactsCache.value + result.data
            }
        }
    }
    
    override suspend fun deleteContact(contactId: String) {
        withContext(Dispatchers.IO) {
            val result = removeContact(contactId)
            if (result is NetworkResult.Success) {
                contactsCache.value = contactsCache.value.filter { it.id != contactId }
            }
        }
    }
    
    override suspend fun updateContact(contact: Contact) {
        withContext(Dispatchers.IO) {
            val result = modifyContact(contact)
            if (result is NetworkResult.Success) {
                contactsCache.value = contactsCache.value.map { 
                    if (it.id == contact.id) result.data else it 
                }
            }
        }
    }
        
    suspend fun fetchAllContacts(): NetworkResult<List<Contact>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.getAllUsers()
                if (response.isSuccessful && response.body()?.success == true) {
                    val users = response.body()?.data?.users ?: emptyList()
                    val contacts = users.map { it.toContact() }
                    NetworkResult.Success(contacts)
                } else {
                    val errorMessage = response.body()?.messages?.firstOrNull() 
                        ?: "Failed to fetch contacts"
                    NetworkResult.Error(errorMessage, response.code())
                }
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "Network error occurred")
            }
        }
    }
    
    suspend fun fetchContactById(contactId: String): NetworkResult<Contact> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.getUserById(contactId)
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let { userDto ->
                        NetworkResult.Success(userDto.toContact())
                    } ?: NetworkResult.Error("Contact not found")
                } else {
                    val errorMessage = response.body()?.messages?.firstOrNull() 
                        ?: "Failed to fetch contact"
                    NetworkResult.Error(errorMessage, response.code())
                }
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "Network error occurred")
            }
        }
    }
    
    suspend fun createContact(contact: Contact): NetworkResult<Contact> {
        return withContext(Dispatchers.IO) {
            try {
                val request = UserRequest(
                    firstName = contact.firstName,
                    lastName = contact.lastName,
                    phoneNumber = contact.phoneNumber,
                    profileImageUrl = contact.photoUri?.toString()
                )
                val response = userApi.createUser(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let { userDto ->
                        NetworkResult.Success(userDto.toContact())
                    } ?: NetworkResult.Error("Failed to create contact")
                } else {
                    val errorMessage = response.body()?.messages?.firstOrNull() 
                        ?: "Failed to create contact"
                    NetworkResult.Error(errorMessage, response.code())
                }
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "Network error occurred")
            }
        }
    }
    
    suspend fun modifyContact(contact: Contact): NetworkResult<Contact> {
        return withContext(Dispatchers.IO) {
            try {
                val request = UserRequest(
                    firstName = contact.firstName,
                    lastName = contact.lastName,
                    phoneNumber = contact.phoneNumber,
                    profileImageUrl = contact.photoUri?.toString()
                )
                val response = userApi.updateUser(contact.id, request)
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let { userDto ->
                        NetworkResult.Success(userDto.toContact())
                    } ?: NetworkResult.Error("Failed to update contact")
                } else {
                    val errorMessage = response.body()?.messages?.firstOrNull() 
                        ?: "Failed to update contact"
                    NetworkResult.Error(errorMessage, response.code())
                }
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "Network error occurred")
            }
        }
    }
    
    suspend fun removeContact(contactId: String): NetworkResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.deleteUser(contactId)
                if (response.isSuccessful && response.body()?.success == true) {
                    NetworkResult.Success(Unit)
                } else {
                    val errorMessage = response.body()?.messages?.firstOrNull() 
                        ?: "Failed to delete contact"
                    NetworkResult.Error(errorMessage, response.code())
                }
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "Network error occurred")
            }
        }
    }
    
    suspend fun uploadImage(imageUri: Uri): NetworkResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val file = getFileFromUri(imageUri)
                if (file == null) {
                    return@withContext NetworkResult.Error("Failed to read image file")
                }
                
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("image", file.name, requestBody)
                
                val response = userApi.uploadImage(multipartBody)
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.imageUrl?.let { imageUrl ->
                        NetworkResult.Success(imageUrl)
                    } ?: NetworkResult.Error("Failed to get image URL")
                } else {
                    val errorMessage = response.body()?.messages?.firstOrNull() 
                        ?: "Failed to upload image"
                    NetworkResult.Error(errorMessage, response.code())
                }
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "Network error occurred")
            }
        }
    }
    
    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()
            tempFile
        } catch (e: Exception) {
            null
        }
    }
    
    private fun updateCache(contact: Contact) {
        val currentList = contactsCache.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == contact.id }
        if (index != -1) {
            currentList[index] = contact
        } else {
            currentList.add(contact)
        }
        contactsCache.value = currentList
    }
    
    suspend fun refreshContacts(): NetworkResult<List<Contact>> {
        return fetchAllContacts().also { result ->
            if (result is NetworkResult.Success) {
                contactsCache.value = result.data.sortedBy { it.firstName.lowercase() }
            }
        }
    }
}
