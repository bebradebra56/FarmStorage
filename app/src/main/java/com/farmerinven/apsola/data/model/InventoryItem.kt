package com.farmerinven.apsola.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val categoryId: Long,
    val quantity: Int,
    val status: ItemStatus,
    val lastUsedDate: Long,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class ItemStatus {
    WORKING,      // Исправен
    NEEDS_REPAIR, // Требует ремонта
    OUT_OF_STOCK  // Закончилось
}
