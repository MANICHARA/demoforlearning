package com.example.screentimewidget.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.screentimewidget.R
import com.example.screentimewidget.data.UsageStatsRepository
import com.example.screentimewidget.databinding.ActivityMainBinding
import com.example.screentimewidget.widget.ScreenTimeWidgetProvider

class MainActivity : ComponentActivity() {

	private lateinit var binding: ActivityMainBinding
	private lateinit var usageStatsRepository: UsageStatsRepository

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		usageStatsRepository = UsageStatsRepository(this)

		binding.buttonGrant.setOnClickListener {
			startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
		}

		binding.buttonOpenUsage.setOnClickListener {
			startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
		}
	}

	override fun onResume() {
		super.onResume()
		refreshUi()
	}

	private fun refreshUi() {
		val granted = usageStatsRepository.hasUsageStatsPermission()
		binding.permissionContainer.visibility = if (granted) android.view.View.GONE else android.view.View.VISIBLE
		binding.contentContainer.visibility = if (granted) android.view.View.VISIBLE else android.view.View.GONE

		if (granted) {
			ScreenTimeWidgetProvider.requestUpdateAll(this)
			val ms = usageStatsRepository.getScreenTimeTodayMs() ?: 0L
			binding.textInfo.text = getString(R.string.widget_title)
			binding.textValue.text = com.example.screentimewidget.util.TimeFormatUtils.formatDurationShort(ms)
		}
	}
}