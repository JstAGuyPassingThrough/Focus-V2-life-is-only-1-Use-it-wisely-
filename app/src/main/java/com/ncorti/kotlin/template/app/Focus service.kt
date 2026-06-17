package com.ncorti.kotlin.template.app

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

class FocusService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName?.toString() ?: ""
        
        // Step 1: Are we inside Instagram or YouTube?
        if (packageName.contains("instagram") || packageName.contains("youtube")) {
            
            // Step 2: Did the user just try to swipe/scroll to the next video?
            if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
                
                // Step 3: Check if we are in the "Reels" or "Shorts" section
                val rootNode = rootInActiveWindow ?: return
                val screenText = getAllText(rootNode).lowercase()

                if (screenText.contains("reels") || screenText.contains("shorts")) {
                    // TRAP TRIGGERED! Kick them to the home screen!
                    performGlobalAction(GLOBAL_ACTION_HOME)
                    Toast.makeText(applicationContext, "One and Done! No doomscrolling allowed!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Our background text scanner
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
