package com.farmerinven.apsola.eiowjf.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class FarmStorageEntity (
    @SerialName("ok")
    val farmStorageOk: Boolean,
    @SerialName("url")
    val farmStorageUrl: String,
    @SerialName("expires")
    val farmStorageExpires: Long,
)