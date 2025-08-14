package com.example.screentimewidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.RemoteViews
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.screentimewidget.R
import com.example.screentimewidget.data.UsageStatsRepository
import com.example.screentimewidget.ui.MainActivity
import com.example.screentimewidget.util.TimeFormatUtils
import java.util.concurrent.TimeUnit

class ScreenTimeWidgetProvider : AppWidgetProvider() {

	override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
		for (id in appWidgetIds) {
			updateAppWidget(context, appWidgetManager, id)
		}
		schedulePeriodicUpdates(context)
	}

	override fun onReceive(context: Context, intent: Intent) {
		super.onReceive(context, intent)
		when (intent.action) {
			ACTION_REFRESH -> requestUpdateAll(context)
		}
	}

	override fun onEnabled(context: Context) {
		schedulePeriodicUpdates(context)
	}

	companion object {
		const val ACTION_REFRESH = "com.example.screentimewidget.ACTION_REFRESH"
		private const val PERIODIC_WORK_NAME = "widget_screen_time_periodic"

		fun requestUpdateAll(context: Context) {
			val manager = AppWidgetManager.getInstance(context)
			val ids = manager.getAppWidgetIds(ComponentName(context, ScreenTimeWidgetProvider::class.java))
			for (id in ids) {
				updateAppWidget(context, manager, id)
			}
		}

		fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
			val repo = UsageStatsRepository(context)
			val views = RemoteViews(context.packageName, R.layout.widget_screen_time)

			val hasPermission = repo.hasUsageStatsPermission()

			if (hasPermission) {
				val ms = repo.getScreenTimeTodayMs() ?: 0L
				views.setTextViewText(R.id.text_time, TimeFormatUtils.formatDurationShort(ms))
				views.setTextViewText(R.id.text_updated, context.getString(R.string.last_updated, TimeFormatUtils.formattedNowTime()))
				// Click: open app
				val openAppIntent = Intent(context, MainActivity::class.java)
				val openPending = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
				views.setOnClickPendingIntent(R.id.container, openPending)
			} else {
				views.setTextViewText(R.id.text_time, context.getString(R.string.no_data))
				views.setTextViewText(R.id.text_updated, context.getString(R.string.permission_needed_title))
				// Click: open usage settings
				val settingsIntent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
				val settingsPending = PendingIntent.getActivity(context, 1, settingsIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
				views.setOnClickPendingIntent(R.id.container, settingsPending)
			}

			// Refresh button intent
			val refreshIntent = Intent(context, ScreenTimeWidgetProvider::class.java).apply { action = ACTION_REFRESH }
			val refreshPending = PendingIntent.getBroadcast(context, 2, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
			views.setOnClickPendingIntent(R.id.btn_refresh, refreshPending)

			appWidgetManager.updateAppWidget(appWidgetId, views)
		}

		fun schedulePeriodicUpdates(context: Context) {
			val request = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(2, TimeUnit.HOURS)
				.setInitialDelay(15, TimeUnit.MINUTES)
				.build()
			WorkManager.getInstance(context).enqueueUniquePeriodicWork(
				PERIODIC_WORK_NAME,
				ExistingPeriodicWorkPolicy.KEEP,
				request
			)
		}
	}
}