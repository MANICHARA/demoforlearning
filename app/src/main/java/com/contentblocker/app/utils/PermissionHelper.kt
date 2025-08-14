package com.contentblocker.app.utils

import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat

class PermissionHelper(private val context: Context) {
    
    /**
     * Check if the app has usage stats permission
     */
    fun hasUsageStatsPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        }
        
        return mode == AppOpsManager.MODE_ALLOWED
    }
    
    /**
     * Check if the app has notification permission (Android 13+)
     */
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
    
    /**
     * Check if the app has system alert window permission
     */
    fun hasSystemAlertWindowPermission(): Boolean {
        return Settings.canDrawOverlays(context)
    }
    
    /**
     * Check if the app has accessibility service permission
     */
    fun hasAccessibilityServicePermission(): Boolean {
        val accessibilityEnabled = Settings.Secure.getInt(
            context.contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED, 0
        )
        
        if (accessibilityEnabled == 1) {
            val service = "${context.packageName}/.service.ContentBlockingAccessibilityService"
            val settingValue = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            return settingValue?.contains(service) == true
        }
        return false
    }
    
    /**
     * Check if the app has all required permissions
     */
    fun hasAllRequiredPermissions(): Boolean {
        return hasUsageStatsPermission() &&
                hasNotificationPermission() &&
                hasSystemAlertWindowPermission()
    }
    
    /**
     * Get a list of missing permissions
     */
    fun getMissingPermissions(): List<String> {
        val missingPermissions = mutableListOf<String>()
        
        if (!hasUsageStatsPermission()) {
            missingPermissions.add("Usage Stats")
        }
        
        if (!hasNotificationPermission()) {
            missingPermissions.add("Notifications")
        }
        
        if (!hasSystemAlertWindowPermission()) {
            missingPermissions.add("System Alert Window")
        }
        
        return missingPermissions
    }
    
    /**
     * Check if the device supports the required features
     */
    fun isDeviceSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }
    
    /**
     * Get the minimum required Android version
     */
    fun getMinimumRequiredVersion(): String {
        return "Android 5.0 (API 21)"
    }
    
    /**
     * Check if the app can request usage stats permission
     */
    fun canRequestUsageStatsPermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }
    
    /**
     * Check if the app can request notification permission
     */
    fun canRequestNotificationPermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }
    
    /**
     * Check if the app can request system alert window permission
     */
    fun canRequestSystemAlertWindowPermission(): Boolean {
        return true // This permission is always requestable
    }
    
    /**
     * Get permission request rationale
     */
    fun getPermissionRationale(permission: String): String {
        return when (permission) {
            "Usage Stats" -> "This permission is required to detect when you open Instagram or YouTube and block Reels/Shorts content."
            "Notifications" -> "This permission allows the app to show you when content has been blocked."
            "System Alert Window" -> "This permission allows the app to show blocking overlays when needed."
            else -> "This permission is required for the app to function properly."
        }
    }
}