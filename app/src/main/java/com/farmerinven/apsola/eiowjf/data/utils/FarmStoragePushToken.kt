package com.farmerinven.apsola.eiowjf.data.utils

import android.util.Log
import com.farmerinven.apsola.eiowjf.presentation.app.FarmStorageApplication
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class FarmStoragePushToken {

    suspend fun farmStorageGetToken(
        farmStorageMaxAttempts: Int = 3,
        farmStorageDelayMs: Long = 1500
    ): String {

        repeat(farmStorageMaxAttempts - 1) {
            try {
                val farmStorageToken = FirebaseMessaging.getInstance().token.await()
                return farmStorageToken
            } catch (e: Exception) {
                Log.e(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "Token error (attempt ${it + 1}): ${e.message}")
                delay(farmStorageDelayMs)
            }
        }

        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "Token error final: ${e.message}")
            "null"
        }
    }


}