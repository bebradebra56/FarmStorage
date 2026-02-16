package com.farmerinven.apsola.eiowjf.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.farmerinven.apsola.eiowjf.presentation.app.FarmStorageApplication

class FarmStoragePushHandler {
    fun farmStorageHandlePush(extras: Bundle?) {
        Log.d(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map: MutableMap<String, String?> = HashMap()
            val ks = extras.keySet()
            val iterator: Iterator<String> = ks.iterator()
            while (iterator.hasNext()) {
                val key = iterator.next()
                map[key] = extras.getString(key)
            }
            Log.d(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "Map from Push = $map")
            map.let {
                if (map.containsKey("url")) {
                    FarmStorageApplication.FARM_STORAGE_FB_LI = map["url"]
                    Log.d(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "Push data no!")
        }
    }

}