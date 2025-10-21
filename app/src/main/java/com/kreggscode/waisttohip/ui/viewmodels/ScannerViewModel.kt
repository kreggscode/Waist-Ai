package com.kreggscode.waisttohip.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.kreggscode.waisttohip.data.repository.AIRepository
import com.kreggscode.waisttohip.data.repository.MealRepository
import com.kreggscode.waisttohip.ui.screens.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    val aiRepository: AIRepository,
    private val mealRepository: MealRepository
) : ViewModel() {
    
    suspend fun saveMealToDatabase(items: List<FoodItem>, mealType: String? = null): Long {
        return mealRepository.saveMeal(items, mealType)
    }
}
