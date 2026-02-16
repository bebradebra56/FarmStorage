package com.farmerinven.apsola.eiowjf.data.shar

import android.content.Context
import androidx.core.content.edit

class FarmStorageSharedPreference(context: Context) {
    private val farmStoragePrefs = context.getSharedPreferences("farmStorageSharedPrefsAb", Context.MODE_PRIVATE)

    var farmStorageSavedUrl: String
        get() = farmStoragePrefs.getString(FARM_STORAGE_SAVED_URL, "") ?: ""
        set(value) = farmStoragePrefs.edit { putString(FARM_STORAGE_SAVED_URL, value) }

    var farmStorageExpired : Long
        get() = farmStoragePrefs.getLong(FARM_STORAGE_EXPIRED, 0L)
        set(value) = farmStoragePrefs.edit { putLong(FARM_STORAGE_EXPIRED, value) }

    var farmStorageAppState: Int
        get() = farmStoragePrefs.getInt(FARM_STORAGE_APPLICATION_STATE, 0)
        set(value) = farmStoragePrefs.edit { putInt(FARM_STORAGE_APPLICATION_STATE, value) }

    var farmStorageNotificationRequest: Long
        get() = farmStoragePrefs.getLong(FARM_STORAGE_NOTIFICAITON_REQUEST, 0L)
        set(value) = farmStoragePrefs.edit { putLong(FARM_STORAGE_NOTIFICAITON_REQUEST, value) }

    var farmStorageNotificationState:Int
        get() = farmStoragePrefs.getInt(FARM_STORAGE_NOTIFICATION_STATE, 0)
        set(value) = farmStoragePrefs.edit { putInt(FARM_STORAGE_NOTIFICATION_STATE, value) }

    companion object {
        private const val FARM_STORAGE_NOTIFICATION_STATE = "farmStorageNotificationState"
        private const val FARM_STORAGE_SAVED_URL = "farmStorageSavedUrl"
        private const val FARM_STORAGE_EXPIRED = "farmStorageExpired"
        private const val FARM_STORAGE_APPLICATION_STATE = "farmStorageApplicationState"
        private const val FARM_STORAGE_NOTIFICAITON_REQUEST = "farmStorageNotificationRequest"
    }
}