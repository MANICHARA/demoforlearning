package com.contentblocker.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.contentblocker.app.databinding.ActivityMainBinding
import com.contentblocker.app.service.ContentBlockingService
import com.contentblocker.app.utils.PreferenceManager
import com.contentblocker.app.utils.PermissionHelper

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var permissionHelper: PermissionHelper
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startContentBlockingService()
        } else {
            showPermissionDeniedDialog()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        preferenceManager = PreferenceManager(this)
        permissionHelper = PermissionHelper(this)
        
        setupNavigation()
        checkPermissions()
        setupServiceToggle()
    }
    
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        binding.bottomNavigation.setupWithNavController(navController)
    }
    
    private fun checkPermissions() {
        if (!permissionHelper.hasUsageStatsPermission()) {
            requestUsageStatsPermission()
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    
    private fun requestUsageStatsPermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
        Toast.makeText(
            this,
            "Please grant Usage Access permission to Content Blocker",
            Toast.LENGTH_LONG
        ).show()
    }
    
    private fun setupServiceToggle() {
        binding.fabToggleService.setOnClickListener {
            if (ContentBlockingService.isRunning) {
                stopContentBlockingService()
            } else {
                if (permissionHelper.hasUsageStatsPermission()) {
                    startContentBlockingService()
                } else {
                    requestUsageStatsPermission()
                }
            }
        }
    }
    
    private fun startContentBlockingService() {
        val intent = Intent(this, ContentBlockingService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        preferenceManager.setServiceEnabled(true)
        updateServiceStatus(true)
    }
    
    private fun stopContentBlockingService() {
        val intent = Intent(this, ContentBlockingService::class.java)
        stopService(intent)
        preferenceManager.setServiceEnabled(false)
        updateServiceStatus(false)
    }
    
    private fun updateServiceStatus(isRunning: Boolean) {
        // Update UI to reflect service status
        // This will be handled by the HomeFragment
    }
    
    private fun showPermissionDeniedDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(R.string.permission_required)
            .setMessage(R.string.usage_stats_explanation)
            .setPositiveButton(R.string.settings) { _, _ ->
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    override fun onResume() {
        super.onResume()
        // Check if permissions were granted while app was in background
        if (permissionHelper.hasUsageStatsPermission() && 
            preferenceManager.isServiceEnabled() && 
            !ContentBlockingService.isRunning) {
            startContentBlockingService()
        }
    }
}