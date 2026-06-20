package com.ncorti.kotlin.template.app

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

class FocusService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName?.toString() ?: return
        val isAppTarget = packageName.contains("instagram") || packageName.contains("youtube")
        
        if (isAppTarget && event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            
            val rootNode = rootInActiveWindow ?: return
            
            // YOUTUBE SHORTS TARGET (Still using our text scanner)
            val screenText = getAllText(rootNode).lowercase()
            val isYouTubeShorts = packageName.contains("youtube") && 
                screenText.contains("like this short")
            
            // INSTAGRAM REELS TARGET (New Bottom Bar Check)
            val isInstaReels = packageName.contains("instagram") && 
                isReelsTabSelected(rootNode)
            
            if (isYouTubeShorts || isInstaReels) {
                performGlobalAction(GLOBAL_ACTION_HOME)
                Toast.makeText(
                    applicationContext, 
                    "This time won't come back again!", 
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // New Smart Check: Only looks at the bottom navigation bar!
    private fun isReelsTabSelected(node: AccessibilityNodeInfo?): Boolean {
        if (node == null) return false
        val desc = node.contentDescription?.toString()?.lowercase() ?: ""
        
        // If the Reels tab is currently selected/highlighted, trap triggered!
        if (desc.contains("reels") && node.isSelected) {
            return true
        }
        
        for (i in 0 until node.childCount) {
            if (isReelsTabSelected(node.getChild(i))) return true
        }
        return false
    }

    // Our background text scanner (still needed for YouTube)
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
