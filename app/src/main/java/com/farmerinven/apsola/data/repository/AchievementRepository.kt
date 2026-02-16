package com.farmerinven.apsola.data.repository

import com.farmerinven.apsola.data.dao.AchievementDao
import com.farmerinven.apsola.data.model.Achievement
import kotlinx.coroutines.flow.Flow

class AchievementRepository(private val achievementDao: AchievementDao) {
    fun getAllAchievements(): Flow<List<Achievement>> = achievementDao.getAllAchievements()
    
    fun getUnlockedAchievements(): Flow<List<Achievement>> = achievementDao.getUnlockedAchievements()
    
    suspend fun insertAchievement(achievement: Achievement) = achievementDao.insertAchievement(achievement)
    
    suspend fun updateAchievement(achievement: Achievement) = achievementDao.updateAchievement(achievement)
    
    suspend fun unlockAchievement(achievement: Achievement) {
        val unlockedAchievement = achievement.copy(
            isUnlocked = true,
            unlockedAt = System.currentTimeMillis()
        )
        achievementDao.updateAchievement(unlockedAchievement)
    }
    
    suspend fun getAchievementCount(): Int = achievementDao.getAchievementCount()
}
