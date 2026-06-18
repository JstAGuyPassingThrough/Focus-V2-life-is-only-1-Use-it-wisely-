    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        
        val packageName = event.packageName?.toString() ?: return

        // Step 1: Are we inside Instagram or YouTube?
        if (packageName.contains("instagram") || packageName.contains("youtube")) {
            
            // Step 2: Did the user just try to swipe/scroll?
            if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
                
                val rootNode = rootInActiveWindow ?: return
                val screenText = getAllText(rootNode).lowercase()
                
                // Step 3: The Smart Check
                // Instead of the tab names, we look for UI buttons that ONLY exist inside the Reels/Shorts players
                val isPlayingShortFormVideo = screenText.contains("like this short") || 
                                              screenText.contains("remix") || 
                                              screenText.contains("use audio") ||
                                              screenText.contains("original audio") ||
                                              screenText.contains("audio page")
                
                if (isPlayingShortFormVideo) {
                    // TRAP TRIGGERED! Kick them to the home screen
                    performGlobalAction(GLOBAL_ACTION_HOME)
                    
                    // A custom reminder of what you should be doing instead!
                    Toast.makeText(applicationContext, "One and Done! +1 Physics and Math won't study themselves!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
