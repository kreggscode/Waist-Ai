package com.kreggscode.waisttohip.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesManager(private val context: Context) {
    
    companion object {
        val CALORIE_GOAL = intPreferencesKey("calorie_goal")
        val CALORIE_PERIOD = stringPreferencesKey("calorie_period") // "daily", "monthly", "yearly"
        val MEASUREMENT_UNIT = stringPreferencesKey("measurement_unit") // "inches", "cm"
    }
    
    val calorieGoal: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[CALORIE_GOAL] ?: 2000
    }
    
    val caloriePeriod: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[CALORIE_PERIOD] ?: "daily"
    }
    
    val measurementUnit: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[MEASUREMENT_UNIT] ?: "inches"
    }
    
    suspend fun setCalorieGoal(goal: Int) {
        context.dataStore.edit { preferences ->
            preferences[CALORIE_GOAL] = goal
        }
    }
    
    suspend fun setCaloriePeriod(period: String) {
        context.dataStore.edit { preferences ->
            preferences[CALORIE_PERIOD] = period
        }
    }
    
    suspend fun setMeasurementUnit(unit: String) {
        context.dataStore.edit { preferences ->
            preferences[MEASUREMENT_UNIT] = unit
        }
    }
}
