package com.contentblocker.app.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contentblocker.app.service.ContentBlockingService
import com.contentblocker.app.utils.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    
    private val _serviceStatus = MutableStateFlow(false)
    val serviceStatus: StateFlow<Boolean> = _serviceStatus.asStateFlow()
    
    private val _blockedCount = MutableStateFlow(0)
    val blockedCount: StateFlow<Int> = _blockedCount.asStateFlow()
    
    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()
    
    init {
        updateServiceStatus()
        updateBlockedCount()
    }
    
    fun startService() {
        viewModelScope.launch {
            try {
                // In a real app, you would start the service here
                // For now, we'll just update the status
                _serviceStatus.value = true
                updateBlockedCount()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun stopService() {
        viewModelScope.launch {
            try {
                // In a real app, you would stop the service here
                _serviceStatus.value = false
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun updateServiceStatus() {
        _serviceStatus.value = ContentBlockingService.isRunning
    }
    
    fun updateBlockedCount() {
        // This would typically come from the service or database
        // For now, we'll use a placeholder value
        _blockedCount.value = 0
    }
    
    fun navigateToSettings() {
        _navigationEvent.value = NavigationEvent.NavigateToSettings
    }
    
    fun navigateToStatistics() {
        _navigationEvent.value = NavigationEvent.NavigateToStatistics
    }
    
    fun clearNavigationEvent() {
        _navigationEvent.value = null
    }
    
    sealed class NavigationEvent {
        object NavigateToSettings : NavigationEvent()
        object NavigateToStatistics : NavigationEvent()
    }
    
    override fun onCleared() {
        super.onCleared()
        // Clean up resources if needed
    }
}