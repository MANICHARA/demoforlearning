package com.example.screentimewidget.data

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Process
import java.util.Calendar

class UsageStatsRepository(private val context: Context) {

	fun hasUsageStatsPermission(): Boolean {
		val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
		val mode = appOpsManager.checkOpNoThrow(
			AppOpsManager.OPSTR_GET_USAGE_STATS,
			Process.myUid(),
			context.packageName
		)
		if (mode == AppOpsManager.MODE_ALLOWED) return true

		// Fallback: attempt a tiny query
		return try {
			val manager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
			val now = System.currentTimeMillis()
			val list = manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, now - 60_000, now)
			!list.isNullOrEmpty()
		} catch (t: Throwable) {
			false
		}
	}

	fun getScreenTimeTodayMs(): Long? {
		if (!hasUsageStatsPermission()) return null
		val manager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
		val calendar = Calendar.getInstance()
		calendar.set(Calendar.HOUR_OF_DAY, 0)
		calendar.set(Calendar.MINUTE, 0)
		calendar.set(Calendar.SECOND, 0)
		calendar.set(Calendar.MILLISECOND, 0)
		val start = calendar.timeInMillis
		val end = System.currentTimeMillis()
		val usageList: List<UsageStats> = manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end)
		if (usageList.isNullOrEmpty()) return 0L
		var total = 0L
		for (stats in usageList) {
			total += stats.totalTimeInForeground
		}
		return total
	}
}