package com.ncorti.kotlin.template.app

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

class FocusService : AccessibilityService() {

    private val blockList = listOf("porn", "sex", "nsfw", "adult", "milf", "xxx", "xhamster", "xham", "hentai")
    
    // Your safe topics override the block
    private val allowList = listOf("matrices", "thermodynamics", "quantum numbers", "plus one physics", "class 11")

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Grab the invisible skeleton of the current screen
        val rootNode = rootInActiveWindow ?: return
        
        // Extract all the text from every corner of the screen
        val screenText = getAllText(rootNode).lowercase()

        for (badWord in blockList) {
            // We use word boundaries to check if the blocked word is isolated
            if (screenText.contains(badWord)) {
                
                var isStudyTopic = false
                for (goodWord in allowList) {
                    if (screenText.contains(goodWord)) {
                        isStudyTopic = true
                        break
                    }
                }

                if (!isStudyTopic) {
                    // Kick back to home screen
                    performGlobalAction(GLOBAL_ACTION_HOME)
                    Toast.makeText(applicationContext, "Focus on Goals: Content Blocked!", Toast.LENGTH_SHORT).show()
                    return 
                }
            }
        }
    }

    // A custom scanner that digs through every element on the screen to pull out the text
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
