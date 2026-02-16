package com.farmerinven.apsola.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDate: Long,
    val isCompleted: Boolean = false,
    val repeatInterval: RepeatInterval = RepeatInterval.NONE,
    val createdAt: Long = System.currentTimeMillis()
)

enum class RepeatInterval {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY
}
