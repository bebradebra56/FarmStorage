package com.farmerinven.apsola.utils

import com.farmerinven.apsola.data.model.Achievement
import com.farmerinven.apsola.data.model.ItemStatus
import com.farmerinven.apsola.data.repository.AchievementRepository
import com.farmerinven.apsola.data.repository.CategoryRepository
import com.farmerinven.apsola.data.repository.InventoryRepository
import kotlinx.coroutines.flow.first

class AchievementChecker(
    private val inventoryRepository: InventoryRepository,
    private val categoryRepository: CategoryRepository,
    private val achievementRepository: AchievementRepository
) {
    suspend fun checkAchievements() {
        checkFirstItemAchievement()
        checkAllWorkingAchievement()
        checkCategoryAchievement()
    }

    private suspend fun checkFirstItemAchievement() {
        val items = inventoryRepository.getAllItems().first()
        if (items.isNotEmpty()) {
            val achievements = achievementRepository.getAllAchievements().first()
            val firstStepAchievement = achievements.find { it.name == "First Step" && !it.isUnlocked }
            firstStepAchievement?.let {
                achievementRepository.unlockAchievement(it)
            }
        }
    }

    private suspend fun checkAllWorkingAchievement() {
        val items = inventoryRepository.getAllItems().first()
        if (items.isNotEmpty() && items.all { it.status == ItemStatus.WORKING }) {
            val achievements = achievementRepository.getAllAchievements().first()
            val cleanBarnAchievement = achievements.find { it.name == "Clean Barn" && !it.isUnlocked }
            cleanBarnAchievement?.let {
                achievementRepository.unlockAchievement(it)
            }
        }
    }

    private suspend fun checkCategoryAchievement() {
        val categoryCount = categoryRepository.getCategoryCount()
        if (categoryCount >= 5) {
            val achievements = achievementRepository.getAllAchievements().first()
            val organizedAchievement = achievements.find { it.name == "Organized" && !it.isUnlocked }
            organizedAchievement?.let {
                achievementRepository.unlockAchievement(it)
            }
        }
    }
}
