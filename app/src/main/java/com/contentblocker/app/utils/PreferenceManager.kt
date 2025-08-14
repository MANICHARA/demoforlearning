package com.contentblocker.app.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class PreferenceManager(context: Context) {
    
    companion object {
        private const val KEY_SERVICE_ENABLED = "service_enabled"
        private const val KEY_INSTAGRAM_BLOCKING = "instagram_blocking_enabled"
        private const val KEY_YOUTUBE_BLOCKING = "youtube_blocking_enabled"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_BLOCKING_METHOD = "blocking_method"
        private const val KEY_AUTO_START = "auto_start_enabled"
        private const val KEY_BLOCKED_COUNT_TODAY = "blocked_count_today"
        private const val KEY_TOTAL_BLOCKED_COUNT = "total_blocked_count"
        private const val KEY_LAST_RESET_DATE = "last_reset_date"
        private const val KEY_TIME_SAVED_TODAY = "time_saved_today"
        private const val KEY_TOTAL_TIME_SAVED = "total_time_saved"
    }
    
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    
    // Service control
    fun setServiceEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SERVICE_ENABLED, enabled).apply()
    }
    
    fun isServiceEnabled(): Boolean {
        return prefs.getBoolean(KEY_SERVICE_ENABLED, false)
    }
    
    // Content blocking settings
    fun setInstagramBlockingEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_INSTAGRAM_BLOCKING, enabled).apply()
    }
    
    fun isInstagramBlockingEnabled(): Boolean {
        return prefs.getBoolean(KEY_INSTAGRAM_BLOCKING, true)
    }
    
    fun setYouTubeBlockingEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_YOUTUBE_BLOCKING, enabled).apply()
    }
    
    fun isYouTubeBlockingEnabled(): Boolean {
        return prefs.getBoolean(KEY_YOUTUBE_BLOCKING, true)
    }
    
    // Notification settings
    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }
    
    fun areNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }
    
    // Blocking method
    fun setBlockingMethod(method: String) {
        prefs.edit().putString(KEY_BLOCKING_METHOD, method).apply()
    }
    
    fun getBlockingMethod(): String {
        return prefs.getString(KEY_BLOCKING_METHOD, "redirect") ?: "redirect"
    }
    
    // Auto-start setting
    fun setAutoStartEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_START, enabled).apply()
    }
    
    fun isAutoStartEnabled(): Boolean {
        return prefs.getBoolean(KEY_AUTO_START, false)
    }
    
    // Statistics
    fun incrementBlockedCount() {
        val today = getCurrentDate()
        val lastResetDate = getLastResetDate()
        
        // Reset daily count if it's a new day
        if (today != lastResetDate) {
            setLastResetDate(today)
            setBlockedCountToday(0)
            setTimeSavedToday(0)
        }
        
        // Increment counts
        val currentToday = getBlockedCountToday()
        val currentTotal = getTotalBlockedCount()
        
        setBlockedCountToday(currentToday + 1)
        setTotalBlockedCount(currentTotal + 1)
        
        // Estimate time saved (assuming 30 seconds per blocked content)
        val timeSaved = 30L // seconds
        val currentTimeToday = getTimeSavedToday()
        val currentTotalTime = getTotalTimeSaved()
        
        setTimeSavedToday(currentTimeToday + timeSaved)
        setTotalTimeSaved(currentTotalTime + timeSaved)
    }
    
    fun getBlockedCountToday(): Int {
        return prefs.getInt(KEY_BLOCKED_COUNT_TODAY, 0)
    }
    
    private fun setBlockedCountToday(count: Int) {
        prefs.edit().putInt(KEY_BLOCKED_COUNT_TODAY, count).apply()
    }
    
    fun getTotalBlockedCount(): Int {
        return prefs.getInt(KEY_TOTAL_BLOCKED_COUNT, 0)
    }
    
    private fun setTotalBlockedCount(count: Int) {
        prefs.edit().putInt(KEY_TOTAL_BLOCKED_COUNT, count).apply()
    }
    
    fun getTimeSavedToday(): Long {
        return prefs.getLong(KEY_TIME_SAVED_TODAY, 0)
    }
    
    private fun setTimeSavedToday(time: Long) {
        prefs.edit().putLong(KEY_TIME_SAVED_TODAY, time).apply()
    }
    
    fun getTotalTimeSaved(): Long {
        return prefs.getLong(KEY_TOTAL_TIME_SAVED, 0)
    }
    
    private fun setTotalTimeSaved(time: Long) {
        prefs.edit().putLong(KEY_TOTAL_TIME_SAVED, time).apply()
    }
    
    private fun getLastResetDate(): String {
        return prefs.getString(KEY_LAST_RESET_DATE, "") ?: ""
    }
    
    private fun setLastResetDate(date: String) {
        prefs.edit().putString(KEY_LAST_RESET_DATE, date).apply()
    }
    
    private fun getCurrentDate(): String {
        val today = java.time.LocalDate.now()
        return today.toString()
    }
    
    // Utility methods for time formatting
    fun formatTimeSaved(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m ${secs}s"
            else -> "${secs}s"
        }
    }
    
    // Reset all statistics
    fun resetStatistics() {
        prefs.edit()
            .putInt(KEY_BLOCKED_COUNT_TODAY, 0)
            .putInt(KEY_TOTAL_BLOCKED_COUNT, 0)
            .putLong(KEY_TIME_SAVED_TODAY, 0)
            .putLong(KEY_TOTAL_TIME_SAVED, 0)
            .putString(KEY_LAST_RESET_DATE, "")
            .apply()
    }
    
    // Get weekly statistics
    fun getWeeklyStats(): Map<String, Int> {
        // This is a simplified implementation
        // In a real app, you might want to store daily data and calculate weekly stats
        val today = getBlockedCountToday()
        return mapOf(
            "Monday" to 0,
            "Tuesday" to 0,
            "Wednesday" to 0,
            "Thursday" to 0,
            "Friday" to 0,
            "Saturday" to 0,
            "Sunday" to today
        )
    }
    
    // Get monthly statistics
    fun getMonthlyStats(): Map<String, Int> {
        // Simplified implementation
        val today = getBlockedCountToday()
        return mapOf(
            "Week 1" to 0,
            "Week 2" to 0,
            "Week 3" to 0,
            "Week 4" to today
        )
    }
}