package com.example.reelshortsblocker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.example.reelshortsblocker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
	private lateinit var binding: ActivityMainBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		binding.openAccessibility.setOnClickListener {
			startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
		}

		binding.openYoutube.setOnClickListener {
			packageManager.getLaunchIntentForPackage("com.google.android.youtube")?.let { startActivity(it) }
		}

		binding.openInstagram.setOnClickListener {
			packageManager.getLaunchIntentForPackage("com.instagram.android")?.let { startActivity(it) }
		}
	}
}