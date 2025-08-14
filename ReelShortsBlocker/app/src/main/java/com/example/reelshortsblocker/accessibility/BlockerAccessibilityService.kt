package com.example.reelshortsblocker.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log

class BlockerAccessibilityService : AccessibilityService() {
	private val youtubePackages = setOf("com.google.android.youtube")
	private val instagramPackages = setOf("com.instagram.android")
	private var lastBackActionTimeMs: Long = 0L

	private fun AccessibilityNodeInfo?.descOrText(): String {
		if (this == null) return ""
		val desc = this.contentDescription?.toString() ?: ""
		val text = this.text?.toString() ?: ""
		return "$desc $text".trim()
	}

	override fun onAccessibilityEvent(event: AccessibilityEvent?) {
		if (event == null) return
		val pkg = event.packageName?.toString() ?: return

		if (!youtubePackages.contains(pkg) && !instagramPackages.contains(pkg)) return

		val root = rootInActiveWindow ?: return

		val isYoutubeShorts = detectYouTubeShorts(root)
		val isInstagramReel = detectInstagramReel(root)

		if (isYoutubeShorts || isInstagramReel) {
			val now = System.currentTimeMillis()
			if (now - lastBackActionTimeMs > 1200) {
				lastBackActionTimeMs = now
				performGlobalAction(GLOBAL_ACTION_BACK)
			}
		}
	}

	private fun detectYouTubeShorts(root: AccessibilityNodeInfo): Boolean {
		// Heuristics: look for text/desc "Shorts" and presence of vertical pager or reel icons
		return root.searchForText("Shorts") || root.searchForContentDescription("Shorts") || root.findNodeByViewId("com.google.android.youtube:id/reel_header_container") != null
	}

	private fun detectInstagramReel(root: AccessibilityNodeInfo): Boolean {
		// Heuristics: look for text/desc "Reels" and reel player view ids
		if (root.searchForText("Reels") || root.searchForContentDescription("Reels")) return true
		val possibleIds = listOf(
			"com.instagram.android:id/reels_player_container",
			"com.instagram.android:id/reel_view",
			"com.instagram.android:id/reel_viewer_container"
		)
		return possibleIds.any { id -> root.findNodeByViewId(id) != null }
	}

	private fun AccessibilityNodeInfo.searchForText(query: String): Boolean {
		return findAccessibilityNodeInfosByText(query).isNotEmpty()
	}

	private fun AccessibilityNodeInfo.searchForContentDescription(query: String): Boolean {
		val queue = ArrayDeque<AccessibilityNodeInfo>()
		queue.add(this)
		while (queue.isNotEmpty()) {
			val node = queue.removeFirst()
			if (node.contentDescription?.toString()?.contains(query, ignoreCase = true) == true) {
				return true
			}
			for (i in 0 until (node.childCount)) {
				node.getChild(i)?.let { queue.add(it) }
			}
		}
		return false
	}

	private fun AccessibilityNodeInfo.findNodeByViewId(id: String): AccessibilityNodeInfo? {
		return try { findAccessibilityNodeInfosByViewId(id).firstOrNull() } catch (_: Exception) { null }
	}

	override fun onInterrupt() { }
}