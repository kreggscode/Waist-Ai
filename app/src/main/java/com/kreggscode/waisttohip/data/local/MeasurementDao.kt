package com.kreggscode.waisttohip.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {
    @Insert
    suspend fun insertMeasurement(measurement: MeasurementEntity): Long
    
    @Query("SELECT * FROM measurements ORDER BY timestamp DESC LIMIT 10")
    fun getRecentMeasurements(): Flow<List<MeasurementEntity>>
    
    @Query("SELECT * FROM measurements ORDER BY timestamp DESC")
    fun getAllMeasurements(): Flow<List<MeasurementEntity>>
    
    @Delete
    suspend fun deleteMeasurement(measurement: MeasurementEntity)
    
    @Query("DELETE FROM measurements")
    suspend fun deleteAllMeasurements()
}
