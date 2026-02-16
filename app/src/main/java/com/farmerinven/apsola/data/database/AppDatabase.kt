package com.farmerinven.apsola.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.farmerinven.apsola.data.dao.*
import com.farmerinven.apsola.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        InventoryItem::class,
        Category::class,
        Reminder::class,
        ItemHistory::class,
        Achievement::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun inventoryItemDao(): InventoryItemDao
    abstract fun categoryDao(): CategoryDao
    abstract fun reminderDao(): ReminderDao
    abstract fun itemHistoryDao(): ItemHistoryDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "farmer_inventory_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database)
                    }
                }
            }
        }

        private suspend fun populateDatabase(database: AppDatabase) {
            val categoryDao = database.categoryDao()
            val achievementDao = database.achievementDao()

            // Populate default categories
            val defaultCategories = listOf(
                Category(name = "Tools", iconName = "construction", colorHex = "#9E9E9E"),
                Category(name = "Feed", iconName = "grass", colorHex = "#66BB6A"),
                Category(name = "Electrical", iconName = "lightbulb", colorHex = "#F4B400"),
                Category(name = "Machinery", iconName = "agriculture", colorHex = "#F28C38"),
                Category(name = "Supplies", iconName = "inventory_2", colorHex = "#3E7BB6"),
                Category(name = "Other", iconName = "more_horiz", colorHex = "#6B6B6B")
            )
            
            defaultCategories.forEach { categoryDao.insertCategory(it) }

            // Populate achievements
            val achievements = listOf(
                Achievement(
                    name = "Good Keeper",
                    description = "7 days without broken items",
                    iconName = "verified"
                ),
                Achievement(
                    name = "Clean Barn",
                    description = "All items checked and working",
                    iconName = "cleaning_services"
                ),
                Achievement(
                    name = "First Step",
                    description = "Add your first item",
                    iconName = "egg"
                ),
                Achievement(
                    name = "Organized",
                    description = "Create 5 categories",
                    iconName = "folder_special"
                )
            )
            
            achievements.forEach { achievementDao.insertAchievement(it) }
        }
    }
}
