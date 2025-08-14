package com.contentblocker.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.contentblocker.app.MainActivity
import com.contentblocker.app.R
import com.contentblocker.app.utils.PreferenceManager
import com.contentblocker.app.utils.ContentDetector
import com.contentblocker.app.utils.BlockingMethod
import kotlinx.coroutines.*

class ContentBlockingService : Service() {
    
    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "content_blocker_channel"
        var isRunning = false
    }
    
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var contentDetector: ContentDetector
    private lateinit var usageStatsManager: UsageStatsManager
    private lateinit var notificationManager: NotificationManager
    
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val handler = Handler(Looper.getMainLooper())
    private var monitoringJob: Job? = null
    
    override fun onCreate() {
        super.onCreate()
        preferenceManager = PreferenceManager(this)
        contentDetector = ContentDetector()
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRunning = true
        startForeground(NOTIFICATION_ID, createNotification())
        startMonitoring()
        return START_STICKY
    }
    
    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        stopMonitoring()
        serviceScope.cancel()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_description)
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.service_running))
            .setSmallIcon(R.drawable.ic_shield)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
    
    private fun startMonitoring() {
        monitoringJob = serviceScope.launch {
            while (isActive) {
                checkCurrentApp()
                delay(MONITORING_INTERVAL)
            }
        }
    }
    
    private fun stopMonitoring() {
        monitoringJob?.cancel()
        monitoringJob = null
    }
    
    private suspend fun checkCurrentApp() {
        try {
            val currentApp = getCurrentForegroundApp()
            if (currentApp != null) {
                when (currentApp) {
                    "com.instagram.android" -> {
                        if (preferenceManager.isInstagramBlockingEnabled()) {
                            checkInstagramContent()
                        }
                    }
                    "com.google.android.youtube" -> {
                        if (preferenceManager.isYouTubeBlockingEnabled()) {
                            checkYouTubeContent()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Log error but continue monitoring
        }
    }
    
    private fun getCurrentForegroundApp(): String? {
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 1000 // Check last 1 second
        
        val events = usageStatsManager.queryEvents(beginTime, endTime)
        val event = UsageEvents.Event()
        var lastEvent: UsageEvents.Event? = null
        
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                lastEvent = UsageEvents.Event().apply {
                    packageName = event.packageName
                    eventType = event.eventType
                    timeStamp = event.timeStamp
                }
            }
        }
        
        return lastEvent?.packageName
    }
    
    private suspend fun checkInstagramContent() {
        if (contentDetector.isInstagramReelsActive()) {
            blockContent(BlockingMethod.INSTAGRAM_REELS)
        }
    }
    
    private suspend fun checkYouTubeContent() {
        if (contentDetector.isYouTubeShortsActive()) {
            blockContent(BlockingMethod.YOUTUBE_SHORTS)
        }
    }
    
    private fun blockContent(method: BlockingMethod) {
        handler.post {
            when (preferenceManager.getBlockingMethod()) {
                "redirect" -> redirectToHome()
                "close" -> closeCurrentApp()
                "alert" -> showBlockingAlert(method)
            }
            
            // Update statistics
            preferenceManager.incrementBlockedCount()
            
            // Show notification if enabled
            if (preferenceManager.areNotificationsEnabled()) {
                showBlockingNotification(method)
            }
        }
    }
    
    private fun redirectToHome() {
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(homeIntent)
    }
    
    private fun closeCurrentApp() {
        // This is a simplified approach - in a real app you might need more sophisticated methods
        redirectToHome()
    }
    
    private fun showBlockingAlert(method: BlockingMethod) {
        val message = when (method) {
            BlockingMethod.INSTAGRAM_REELS -> "Instagram Reels detected and blocked!"
            BlockingMethod.YOUTUBE_SHORTS -> "YouTube Shorts detected and blocked!"
        }
        
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    private fun showBlockingNotification(method: BlockingMethod) {
        val content = when (method) {
            BlockingMethod.INSTAGRAM_REELS -> "Instagram Reels blocked"
            BlockingMethod.YOUTUBE_SHORTS -> "YouTube Shorts blocked"
        }
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_shield)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
    
    companion object {
        private const val MONITORING_INTERVAL = 1000L // 1 second
    }
}