package com.ncorti.kotlin.template.app

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

class FocusService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName?.toString() ?: return

        // Step 1: Are we inside Instagram or YouTube?
        val isAppTarget = packageName.contains("instagram") || packageName.contains("youtube")
        
        if (isAppTarget) {
            // Step 2: Did the user just try to swipe/scroll?
            if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
                
                // --- THE TRIPWIRE ---
                // If you see this pop up, the M31 successfully detects your swipe!
                Toast.makeText(applicationContext, "Bouncer heard a swipe!", Toast.LENGTH_SHORT).show()
                
                val rootNode = rootInActiveWindow ?: return
                val screenText = getAllText(rootNode).lowercase()
                
                // Step 3: The Smart Check
                val isShorts = screenText.contains("like this short") || 
                    screenText.contains("remix") || 
                    screenText.contains("use audio") ||
                    screenText.contains("original audio") ||
                    screenText.contains("audio page")
                
                if (isShorts) {
                    performGlobalAction(GLOBAL_ACTION_HOME)
                    
                    Toast.makeText(
                        applicationContext, 
                        "One and Done! +1 Physics and Math won't study themselves!", 
                        Toast.LENGTH_SHORT
                    ).show()
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
