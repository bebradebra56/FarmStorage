package com.farmerinven.apsola.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_history")
data class ItemHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemId: Long,
    val action: String,
    val timestamp: Long = System.currentTimeMillis()
)
