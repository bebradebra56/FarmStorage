package com.farmerinven.apsola.data.dao

import androidx.room.*
import com.farmerinven.apsola.data.model.Achievement
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): Flow<List<Achievement>>
    
    @Query("SELECT * FROM achievements WHERE isUnlocked = 1")
    fun getUnlockedAchievements(): Flow<List<Achievement>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: Achievement)
    
    @Update
    suspend fun updateAchievement(achievement: Achievement)
    
    @Query("SELECT COUNT(*) FROM achievements")
    suspend fun getAchievementCount(): Int
}
