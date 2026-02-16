package com.farmerinven.apsola.data.dao

import androidx.room.*
import com.farmerinven.apsola.data.model.ItemHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemHistoryDao {
    @Query("SELECT * FROM item_history WHERE itemId = :itemId ORDER BY timestamp DESC")
    fun getHistoryForItem(itemId: Long): Flow<List<ItemHistory>>
    
    @Insert
    suspend fun insertHistory(history: ItemHistory)
    
    @Query("DELETE FROM item_history WHERE itemId = :itemId")
    suspend fun deleteHistoryForItem(itemId: Long)
}
