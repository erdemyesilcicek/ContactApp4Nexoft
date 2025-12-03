package com.erdemyesilcicek.contactapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "search_history")

@Singleton
class SearchHistoryRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val SEARCH_HISTORY_KEY = stringPreferencesKey("search_history")
        private const val MAX_HISTORY_SIZE = 10
        private const val SEPARATOR = "|||"
    }
    
    val searchHistory: Flow<List<String>> = context.dataStore.data.map { preferences ->
        val historyString = preferences[SEARCH_HISTORY_KEY] ?: ""
        if (historyString.isEmpty()) {
            emptyList()
        } else {
            historyString.split(SEPARATOR).reversed()
        }
    }
    
    suspend fun addSearchQuery(query: String) {
        if (query.isBlank()) return
        
        context.dataStore.edit { preferences ->
            val historyString = preferences[SEARCH_HISTORY_KEY] ?: ""
            val currentHistory = if (historyString.isEmpty()) {
                mutableListOf()
            } else {
                historyString.split(SEPARATOR).toMutableList()
            }
            
            // Remove if already exists (to move it to the end)
            currentHistory.remove(query.trim())
            
            // Add new query
            currentHistory.add(query.trim())
            
            // Keep only last MAX_HISTORY_SIZE items
            val limitedHistory = if (currentHistory.size > MAX_HISTORY_SIZE) {
                currentHistory.takeLast(MAX_HISTORY_SIZE)
            } else {
                currentHistory
            }
            
            preferences[SEARCH_HISTORY_KEY] = limitedHistory.joinToString(SEPARATOR)
        }
    }
    
    suspend fun removeSearchQuery(query: String) {
        context.dataStore.edit { preferences ->
            val historyString = preferences[SEARCH_HISTORY_KEY] ?: ""
            val currentHistory = if (historyString.isEmpty()) {
                mutableListOf()
            } else {
                historyString.split(SEPARATOR).toMutableList()
            }
            currentHistory.remove(query)
            preferences[SEARCH_HISTORY_KEY] = currentHistory.joinToString(SEPARATOR)
        }
    }
    
    suspend fun clearHistory() {
        context.dataStore.edit { preferences ->
            preferences.remove(SEARCH_HISTORY_KEY)
        }
    }
}
