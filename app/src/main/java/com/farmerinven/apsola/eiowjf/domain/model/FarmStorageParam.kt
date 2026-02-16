package com.farmerinven.apsola.eiowjf.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


private const val FARM_STORAGE_A = "com.farmerinven.apsola"
private const val FARM_STORAGE_B = "farmstorage-d1658"
@Serializable
data class FarmStorageParam (
    @SerialName("af_id")
    val farmStorageAfId: String,
    @SerialName("bundle_id")
    val farmStorageBundleId: String = FARM_STORAGE_A,
    @SerialName("os")
    val farmStorageOs: String = "Android",
    @SerialName("store_id")
    val farmStorageStoreId: String = FARM_STORAGE_A,
    @SerialName("locale")
    val farmStorageLocale: String,
    @SerialName("push_token")
    val farmStoragePushToken: String,
    @SerialName("firebase_project_id")
    val farmStorageFirebaseProjectId: String = FARM_STORAGE_B,
    )