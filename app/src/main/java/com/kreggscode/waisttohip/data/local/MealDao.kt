package com.kreggscode.waisttohip.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Insert
    suspend fun insertMeal(meal: MealEntity): Long
    
    @Query("SELECT * FROM meals ORDER BY timestamp DESC LIMIT 10")
    fun getRecentMeals(): Flow<List<MealEntity>>
    
    @Query("SELECT * FROM meals WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getMealsByDateRange(startTime: Long, endTime: Long): Flow<List<MealEntity>>
    
    @Query("SELECT * FROM meals ORDER BY timestamp DESC")
    fun getAllMeals(): Flow<List<MealEntity>>
    
    @Delete
    suspend fun deleteMeal(meal: MealEntity)
    
    @Query("DELETE FROM meals")
    suspend fun deleteAllMeals()
}
