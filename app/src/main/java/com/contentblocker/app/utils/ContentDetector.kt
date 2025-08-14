package com.contentblocker.app.utils

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContentDetector {
    
    companion object {
        private const val INSTAGRAM_PACKAGE = "com.instagram.android"
        private const val YOUTUBE_PACKAGE = "com.google.android.youtube"
        private const val REELS_TAB_TEXT = "Reels"
        private const val SHORTS_TAB_TEXT = "Shorts"
    }
    
    // This is a simplified detection method
    // In a real implementation, you would need more sophisticated detection
    // such as accessibility services or screen content analysis
    
    suspend fun isInstagramReelsActive(): Boolean = withContext(Dispatchers.IO) {
        // For now, we'll use a simplified approach
        // In a real app, you would need to implement accessibility service
        // or use other methods to detect the actual content being viewed
        
        try {
            // This is a placeholder implementation
            // Real detection would require accessibility service or screen analysis
            false
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun isYouTubeShortsActive(): Boolean = withContext(Dispatchers.IO) {
        // Similar to Instagram detection, this is a placeholder
        // Real implementation would require accessibility service
        
        try {
            // Placeholder implementation
            false
        } catch (e: Exception) {
            false
        }
    }
    
    // Alternative detection methods that could be implemented:
    
    fun detectByAccessibilityService(event: AccessibilityEvent): Boolean {
        return when (event.packageName) {
            INSTAGRAM_PACKAGE -> detectInstagramReels(event)
            YOUTUBE_PACKAGE -> detectYouTubeShorts(event)
            else -> false
        }
    }
    
    private fun detectInstagramReels(event: AccessibilityEvent): Boolean {
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            
            val source = event.source ?: return false
            
            // Look for Reels-related text or elements
            val reelsNodes = source.findAccessibilityNodeInfosByText(REELS_TAB_TEXT)
            if (reelsNodes.isNotEmpty()) {
                // Check if Reels tab is selected/active
                for (node in reelsNodes) {
                    if (node.isSelected || node.isFocused) {
                        return true
                    }
                }
            }
            
            // Look for Reels-specific content indicators
            val reelsContent = source.findAccessibilityNodeInfosByText("reel")
            if (reelsContent.isNotEmpty()) {
                return true
            }
        }
        return false
    }
    
    private fun detectYouTubeShorts(event: AccessibilityEvent): Boolean {
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            
            val source = event.source ?: return false
            
            // Look for Shorts-related text or elements
            val shortsNodes = source.findAccessibilityNodeInfosByText(SHORTS_TAB_TEXT)
            if (shortsNodes.isNotEmpty()) {
                // Check if Shorts tab is selected/active
                for (node in shortsNodes) {
                    if (node.isSelected || node.isFocused) {
                        return true
                    }
                }
            }
            
            // Look for Shorts-specific content indicators
            val shortsContent = source.findAccessibilityNodeInfosByText("shorts")
            if (shortsContent.isNotEmpty()) {
                return true
            }
            
            // Check for Shorts URL patterns
            val urlNodes = source.findAccessibilityNodeInfosByText("youtube.com/shorts")
            if (urlNodes.isNotEmpty()) {
                return true
            }
        }
        return false
    }
    
    // Method to check if accessibility service is enabled
    fun isAccessibilityServiceEnabled(context: Context): Boolean {
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
}