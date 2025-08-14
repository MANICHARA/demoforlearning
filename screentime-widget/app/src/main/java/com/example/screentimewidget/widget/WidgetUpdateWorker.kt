package com.example.screentimewidget.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class WidgetUpdateWorker(
	private val appContext: Context,
	params: WorkerParameters
) : CoroutineWorker(appContext, params) {

	override suspend fun doWork(): Result {
		val manager = AppWidgetManager.getInstance(appContext)
		val ids = manager.getAppWidgetIds(ComponentName(appContext, ScreenTimeWidgetProvider::class.java))
		for (id in ids) {
			ScreenTimeWidgetProvider.updateAppWidget(appContext, manager, id)
		}
		return Result.success()
	}
}