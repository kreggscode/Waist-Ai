package com.kreggscode.waisttohip.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreggscode.waisttohip.data.local.MealEntity
import com.kreggscode.waisttohip.data.local.MeasurementEntity
import com.kreggscode.waisttohip.data.repository.MealRepository
import com.kreggscode.waisttohip.data.repository.MeasurementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mealRepository: MealRepository,
    private val measurementRepository: MeasurementRepository
) : ViewModel() {
    
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
}
