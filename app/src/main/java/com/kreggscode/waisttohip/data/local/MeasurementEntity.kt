package com.kreggscode.waisttohip.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "measurements")
data class MeasurementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val waistInches: Float,
    val hipInches: Float,
    val whrValue: Float
)
