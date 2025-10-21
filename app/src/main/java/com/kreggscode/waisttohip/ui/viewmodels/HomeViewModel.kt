package com.kreggscode.waisttohip.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreggscode.waisttohip.data.local.MealEntity
import com.kreggscode.waisttohip.data.local.MeasurementEntity
import com.kreggscode.waisttohip.data.local.UserPreferencesManager
import com.kreggscode.waisttohip.data.repository.MealRepository
import com.kreggscode.waisttohip.data.repository.MeasurementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mealRepository: MealRepository,
    private val measurementRepository: MeasurementRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val preferencesManager = UserPreferencesManager(context)
    
    val recentMeals: StateFlow<List<MealEntity>> = mealRepository.getRecentMeals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val recentMeasurements: StateFlow<List<MeasurementEntity>> = measurementRepository.getRecentMeasurements()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val calorieGoal: StateFlow<Int> = preferencesManager.calorieGoal
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 2000
        )
    
    val caloriePeriod: StateFlow<String> = preferencesManager.caloriePeriod
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "daily"
        )
    
    fun updateCalorieGoal(goal: Int) {
        viewModelScope.launch {
            preferencesManager.setCalorieGoal(goal)
        }
    }
    
    fun updateCaloriePeriod(period: String) {
        viewModelScope.launch {
            preferencesManager.setCaloriePeriod(period)
        }
    }
    
    suspend fun addManualMeal(
        mealName: String,
        calories: Int,
        protein: Int,
        carbs: Int,
        fat: Int,
        mealType: String
    ) {
        // Create a FoodItem for the manual meal
        val foodItem = com.kreggscode.waisttohip.ui.screens.FoodItem(
            id = java.util.UUID.randomUUID().toString(),
            name = mealName,
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat,
            quantity = 1f
        )
        
        // Use the existing saveMeal method
        mealRepository.saveMeal(listOf(foodItem), mealType)
    }
}
