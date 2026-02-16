package com.farmerinven.apsola.data.dao

import androidx.room.*
import com.farmerinven.apsola.data.model.InventoryItem
import com.farmerinven.apsola.data.model.ItemStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryItemDao {
    @Query("SELECT * FROM inventory_items ORDER BY createdAt DESC")
    fun getAllItems(): Flow<List<InventoryItem>>
    
    @Query("SELECT * FROM inventory_items WHERE id = :id")
    suspend fun getItemById(id: Long): InventoryItem?
    
    @Query("SELECT * FROM inventory_items WHERE categoryId = :categoryId")
    fun getItemsByCategory(categoryId: Long): Flow<List<InventoryItem>>
    
    @Query("SELECT * FROM inventory_items WHERE status = :status")
    fun getItemsByStatus(status: ItemStatus): Flow<List<InventoryItem>>
    
    @Query("SELECT COUNT(*) FROM inventory_items WHERE status = :status")
    fun getItemCountByStatus(status: ItemStatus): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: InventoryItem): Long
    
    @Update
    suspend fun updateItem(item: InventoryItem)
    
    @Delete
    suspend fun deleteItem(item: InventoryItem)
    
    @Query("DELETE FROM inventory_items")
    suspend fun deleteAllItems()
    
    @Query("SELECT * FROM inventory_items WHERE name LIKE '%' || :query || '%'")
    fun searchItems(query: String): Flow<List<InventoryItem>>
}
