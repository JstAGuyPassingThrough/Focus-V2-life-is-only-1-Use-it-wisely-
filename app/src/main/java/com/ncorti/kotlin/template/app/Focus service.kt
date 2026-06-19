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
                
                val rootNode = rootInActiveWindow ?: return
                val screenText = getAllText(rootNode).lowercase()
                
                // Step 3: The Smart Check
                // We removed the 'audio' triggers so normal photo posts don't kick you!
                val isShorts = screenText.contains("like this short") || 
                    screenText.contains("remix")
                
                if (isShorts) {
                    performGlobalAction(GLOBAL_ACTION_HOME)
                    
                    Toast.makeText(
                        applicationContext, 
                        "This time won't come back again!", 
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
