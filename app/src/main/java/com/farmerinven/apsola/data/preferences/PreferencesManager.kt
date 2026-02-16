package com.farmerinven.apsola.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    
    companion object {
        val THEME_KEY = stringPreferencesKey("theme")
        val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
        val CURRENCY_KEY = stringPreferencesKey("currency")
        val GOLDEN_EGG_TAPS_KEY = intPreferencesKey("golden_egg_taps")
        val GOLDEN_EGG_SHOWN_KEY = booleanPreferencesKey("golden_egg_shown")
    }
    
    val theme: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_KEY] ?: "light"
    }
    
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_ENABLED_KEY] ?: true
    }
    
    val currency: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[CURRENCY_KEY] ?: "USD"
    }
    
    val goldenEggTaps: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[GOLDEN_EGG_TAPS_KEY] ?: 0
    }
    
    val goldenEggShown: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[GOLDEN_EGG_SHOWN_KEY] ?: false
    }
    
    suspend fun setTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }
    
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED_KEY] = enabled
        }
    }
    
    suspend fun setCurrency(currency: String) {
        context.dataStore.edit { preferences ->
            preferences[CURRENCY_KEY] = currency
        }
    }
    
    suspend fun incrementGoldenEggTaps() {
        context.dataStore.edit { preferences ->
            val currentTaps = preferences[GOLDEN_EGG_TAPS_KEY] ?: 0
            preferences[GOLDEN_EGG_TAPS_KEY] = currentTaps + 1
        }
    }
    
    suspend fun setGoldenEggShown(shown: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[GOLDEN_EGG_SHOWN_KEY] = shown
        }
    }
    
    suspend fun resetGoldenEggTaps() {
        context.dataStore.edit { preferences ->
            preferences[GOLDEN_EGG_TAPS_KEY] = 0
        }
    }
    
    suspend fun clearAllData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
