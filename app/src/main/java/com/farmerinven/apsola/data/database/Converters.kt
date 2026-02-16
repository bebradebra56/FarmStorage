package com.farmerinven.apsola.data.database

import androidx.room.TypeConverter
import com.farmerinven.apsola.data.model.ItemStatus
import com.farmerinven.apsola.data.model.RepeatInterval

class Converters {
    @TypeConverter
    fun fromItemStatus(value: ItemStatus): String {
        return value.name
    }

    @TypeConverter
    fun toItemStatus(value: String): ItemStatus {
        return ItemStatus.valueOf(value)
    }

    @TypeConverter
    fun fromRepeatInterval(value: RepeatInterval): String {
        return value.name
    }

    @TypeConverter
    fun toRepeatInterval(value: String): RepeatInterval {
        return RepeatInterval.valueOf(value)
    }
}
