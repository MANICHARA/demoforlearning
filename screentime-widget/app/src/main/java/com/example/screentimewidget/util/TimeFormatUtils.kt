package com.example.screentimewidget.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeFormatUtils {
	fun formatDurationShort(milliseconds: Long): String {
		val totalSeconds = milliseconds / 1000
		val hours = totalSeconds / 3600
		val minutes = (totalSeconds % 3600) / 60
		return if (hours > 0) {
			"${hours}h ${minutes}m"
		} else {
			"${minutes}m"
		}
	}

	fun formattedNowTime(): String {
		val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
		return sdf.format(Date())
	}
}