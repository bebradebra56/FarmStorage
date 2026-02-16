package com.farmerinven.apsola.data.repository

import com.farmerinven.apsola.data.dao.InventoryItemDao
import com.farmerinven.apsola.data.dao.ItemHistoryDao
import com.farmerinven.apsola.data.model.InventoryItem
import com.farmerinven.apsola.data.model.ItemHistory
import com.farmerinven.apsola.data.model.ItemStatus
import kotlinx.coroutines.flow.Flow

class InventoryRepository(
    private val inventoryItemDao: InventoryItemDao,
    private val itemHistoryDao: ItemHistoryDao
) {
    fun getAllItems(): Flow<List<InventoryItem>> = inventoryItemDao.getAllItems()
    
    suspend fun getItemById(id: Long): InventoryItem? = inventoryItemDao.getItemById(id)
    
    fun getItemsByCategory(categoryId: Long): Flow<List<InventoryItem>> =
        inventoryItemDao.getItemsByCategory(categoryId)
    
    fun getItemsByStatus(status: ItemStatus): Flow<List<InventoryItem>> =
        inventoryItemDao.getItemsByStatus(status)
    
    fun getItemCountByStatus(status: ItemStatus): Flow<Int> =
        inventoryItemDao.getItemCountByStatus(status)
    
    suspend fun insertItem(item: InventoryItem): Long {
        val id = inventoryItemDao.insertItem(item)
        itemHistoryDao.insertHistory(
            ItemHistory(itemId = id, action = "Created")
        )
        return id
    }
    
    suspend fun updateItem(item: InventoryItem) {
        inventoryItemDao.updateItem(item)
        itemHistoryDao.insertHistory(
            ItemHistory(itemId = item.id, action = "Updated")
        )
    }
    
    suspend fun deleteItem(item: InventoryItem) {
        inventoryItemDao.deleteItem(item)
        itemHistoryDao.deleteHistoryForItem(item.id)
    }
    
    suspend fun deleteAllItems() {
        inventoryItemDao.deleteAllItems()
    }
    
    fun searchItems(query: String): Flow<List<InventoryItem>> =
        inventoryItemDao.searchItems(query)
    
    fun getHistoryForItem(itemId: Long): Flow<List<ItemHistory>> =
        itemHistoryDao.getHistoryForItem(itemId)
}
