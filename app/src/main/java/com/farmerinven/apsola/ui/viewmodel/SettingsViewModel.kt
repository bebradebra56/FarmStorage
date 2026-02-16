package com.farmerinven.apsola.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farmerinven.apsola.data.preferences.PreferencesManager
import com.farmerinven.apsola.data.repository.InventoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val theme: String = "light",
    val notificationsEnabled: Boolean = true,
    val currency: String = "USD",
    val goldenEggTaps: Int = 0,
    val goldenEggShown: Boolean = false
)

class SettingsViewModel(
    private val preferencesManager: PreferencesManager,
    private val inventoryRepository: InventoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                preferencesManager.theme,
                preferencesManager.notificationsEnabled,
                preferencesManager.currency,
                preferencesManager.goldenEggTaps,
                preferencesManager.goldenEggShown
            ) { theme, notificationsEnabled, currency, goldenEggTaps, goldenEggShown ->
                SettingsUiState(
                    theme = theme,
                    notificationsEnabled = notificationsEnabled,
                    currency = currency,
                    goldenEggTaps = goldenEggTaps,
                    goldenEggShown = goldenEggShown
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch {
            preferencesManager.setTheme(theme)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setNotificationsEnabled(enabled)
        }
    }

    fun setCurrency(currency: String) {
        viewModelScope.launch {
            preferencesManager.setCurrency(currency)
        }
    }

    fun incrementGoldenEggTaps() {
        viewModelScope.launch {
            preferencesManager.incrementGoldenEggTaps()
            if (_uiState.value.goldenEggTaps + 1 >= 7 && !_uiState.value.goldenEggShown) {
                preferencesManager.setGoldenEggShown(true)
            }
        }
    }

    fun resetGoldenEgg() {
        viewModelScope.launch {
            preferencesManager.resetGoldenEggTaps()
            preferencesManager.setGoldenEggShown(false)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            inventoryRepository.deleteAllItems()
            preferencesManager.clearAllData()
        }
    }
}
