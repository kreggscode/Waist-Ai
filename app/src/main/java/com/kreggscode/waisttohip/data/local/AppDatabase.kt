package com.kreggscode.waisttohip.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MealEntity::class, MeasurementEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun measurementDao(): MeasurementDao
}
