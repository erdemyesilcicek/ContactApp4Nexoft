package com.erdemyesilcicek.contactapp.data.repository

import android.content.Context
import android.provider.ContactsContract
import com.erdemyesilcicek.contactapp.util.PhoneUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceContactsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun getAllDevicePhoneNumbers(): Set<String> = withContext(Dispatchers.IO) {
        val phoneNumbers = mutableSetOf<String>()
        
        try {
            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                null,
                null,
                null
            )
            
            cursor?.use {
                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                while (it.moveToNext()) {
                    val number = it.getString(numberIndex)
                    if (!number.isNullOrBlank()) {
                        phoneNumbers.add(PhoneUtils.normalizePhoneNumber(number))
                    }
                }
            }
        } catch (e: SecurityException) {
        } catch (e: Exception) {
        }
        
        phoneNumbers
    }
    
    suspend fun isPhoneNumberInDeviceContacts(phoneNumber: String): Boolean = withContext(Dispatchers.IO) {
        val normalizedNumber = PhoneUtils.normalizePhoneNumber(phoneNumber)
        
        try {
            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                null,
                null,
                null
            )
            
            cursor?.use {
                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                while (it.moveToNext()) {
                    val number = it.getString(numberIndex)
                    if (!number.isNullOrBlank() && PhoneUtils.normalizePhoneNumber(number) == normalizedNumber) {
                        return@withContext true
                    }
                }
            }
        } catch (e: SecurityException) {
        } catch (e: Exception) {
        }
        
        false
    }
}
