package com.ncorti.kotlin.template.app

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

class FocusService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName?.toString() ?: return

        if (packageName.contains("instagram") || packageName.contains("youtube")) {
            if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
                
                val rootNode = rootInActiveWindow ?: return
                val screenText = getAllText(rootNode).lowercase()
                
                // YOUTUBE SHORTS LOGIC (Still works perfectly!)
                if (packageName.contains("youtube") && screenText.contains("like this short")) {
                    performGlobalAction(GLOBAL_ACTION_HOME)
                    Toast.makeText(applicationContext, "This time won't come back again!", Toast.LENGTH_SHORT).show()
                    return
                }

                // INSTAGRAM X-RAY VISION (Debug Mode)
                if (packageName.contains("instagram")) {
                    // This prints the first 150 characters the app "sees" to your screen
                    val xRayText = screenText.take(150) 
                    Toast.makeText(applicationContext, "X-RAY: $xRayText", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getAllText(node: AccessibilityNodeInfo?): String {
        if (node == null) return ""
        var text = node.text?.toString() ?: ""
        text += " " + (node.contentDescription?.toString() ?: "")
        for (i in 0 until node.childCount) {
            text += " " + getAllText(node.getChild(i))
        }
        return text
    }

    override fun onInterrupt() {}
}
