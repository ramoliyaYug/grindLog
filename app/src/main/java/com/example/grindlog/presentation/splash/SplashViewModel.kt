package com.example.grindlog.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grindlog.domain.notification.DailySummaryScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dailySummaryScheduler: DailySummaryScheduler
) : ViewModel() {

    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            try {
                dailySummaryScheduler.scheduleDailySummary()
            } catch (e: Exception) {
            }
        }
    }
}
