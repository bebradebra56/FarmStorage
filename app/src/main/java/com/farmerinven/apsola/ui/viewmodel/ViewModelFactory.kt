package com.farmerinven.apsola.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.farmerinven.apsola.data.database.AppDatabase
import com.farmerinven.apsola.data.preferences.PreferencesManager
import com.farmerinven.apsola.data.repository.*

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    
    private val database by lazy { AppDatabase.getDatabase(context) }
    private val inventoryRepository by lazy {
        InventoryRepository(database.inventoryItemDao(), database.itemHistoryDao())
    }
    private val categoryRepository by lazy {
        CategoryRepository(database.categoryDao())
    }
    private val reminderRepository by lazy {
        ReminderRepository(database.reminderDao())
    }
    private val achievementRepository by lazy {
        AchievementRepository(database.achievementDao())
    }
    private val preferencesManager by lazy {
        PreferencesManager(context)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(InventoryViewModel::class.java) -> {
                InventoryViewModel(inventoryRepository, categoryRepository) as T
            }
            modelClass.isAssignableFrom(ItemDetailViewModel::class.java) -> {
                ItemDetailViewModel(inventoryRepository, categoryRepository) as T
            }
            modelClass.isAssignableFrom(AddEditItemViewModel::class.java) -> {
                AddEditItemViewModel(inventoryRepository, categoryRepository) as T
            }
            modelClass.isAssignableFrom(CategoryViewModel::class.java) -> {
                CategoryViewModel(categoryRepository) as T
            }
            modelClass.isAssignableFrom(ReminderViewModel::class.java) -> {
                ReminderViewModel(reminderRepository) as T
            }
            modelClass.isAssignableFrom(AnalyticsViewModel::class.java) -> {
                AnalyticsViewModel(inventoryRepository, categoryRepository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(preferencesManager, inventoryRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
