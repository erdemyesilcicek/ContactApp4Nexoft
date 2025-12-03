package com.erdemyesilcicek.contactapp.data.repository

import android.content.Context
import android.provider.ContactsContract
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceContactsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    /**
     * Cihaz rehberindeki tüm telefon numaralarını döndürür
     */
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
                        // Telefon numarasını normalize et (boşluk, tire, parantez kaldır)
                        phoneNumbers.add(normalizePhoneNumber(number))
                    }
                }
            }
        } catch (e: SecurityException) {
            // İzin verilmemişse boş set döndür
        } catch (e: Exception) {
            // Diğer hatalar için de boş set döndür
        }
        
        phoneNumbers
    }
    
    /**
     * Belirli bir telefon numarasının cihaz rehberinde olup olmadığını kontrol eder
     */
    suspend fun isPhoneNumberInDeviceContacts(phoneNumber: String): Boolean = withContext(Dispatchers.IO) {
        val normalizedNumber = normalizePhoneNumber(phoneNumber)
        
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
                    if (!number.isNullOrBlank() && normalizePhoneNumber(number) == normalizedNumber) {
                        return@withContext true
                    }
                }
            }
        } catch (e: SecurityException) {
            // İzin verilmemişse false döndür
        } catch (e: Exception) {
            // Diğer hatalar için de false döndür
        }
        
        false
    }
    
    /**
     * Telefon numarasını karşılaştırma için normalize eder
     * Boşluk, tire, parantez gibi karakterleri kaldırır
     */
    private fun normalizePhoneNumber(phoneNumber: String): String {
        return phoneNumber.filter { it.isDigit() || it == '+' }
    }
}
