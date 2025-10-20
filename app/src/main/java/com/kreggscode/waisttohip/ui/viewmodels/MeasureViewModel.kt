package com.kreggscode.waisttohip.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.kreggscode.waisttohip.data.repository.MeasurementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MeasureViewModel @Inject constructor(
    private val measurementRepository: MeasurementRepository
) : ViewModel() {
    
    suspend fun saveMeasurement(waistInches: Float, hipInches: Float): Long {
        return measurementRepository.saveMeasurement(waistInches, hipInches)
    }
}
