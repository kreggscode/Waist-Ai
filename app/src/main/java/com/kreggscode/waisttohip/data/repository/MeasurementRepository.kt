package com.kreggscode.waisttohip.data.repository

import com.kreggscode.waisttohip.data.local.MeasurementDao
import com.kreggscode.waisttohip.data.local.MeasurementEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MeasurementRepository @Inject constructor(
    private val measurementDao: MeasurementDao
) {
    fun getRecentMeasurements(): Flow<List<MeasurementEntity>> = measurementDao.getRecentMeasurements()
    
    fun getAllMeasurements(): Flow<List<MeasurementEntity>> = measurementDao.getAllMeasurements()
    
    suspend fun saveMeasurement(waistInches: Float, hipInches: Float): Long {
        val whrValue = waistInches / hipInches
        
        val measurement = MeasurementEntity(
            timestamp = System.currentTimeMillis(),
            waistInches = waistInches,
            hipInches = hipInches,
            whrValue = whrValue
        )
        
        return measurementDao.insertMeasurement(measurement)
    }
    
    suspend fun deleteMeasurement(measurement: MeasurementEntity) {
        measurementDao.deleteMeasurement(measurement)
    }
}
