package com.edgelighting.app

import android.app.Notification
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.palette.graphics.Palette

class EdgeNotificationListener : NotificationListenerService() {

    companion object {
        private const val TAG = "EdgeNotificationListener"
        const val ACTION_NOTIFICATION_POSTED = "com.edgelighting.app.NOTIFICATION_POSTED"
        const val EXTRA_PACKAGE_NAME = "package_name"
        const val EXTRA_TITLE = "title"
        const val EXTRA_TEXT = "text"
        const val EXTRA_COLOR = "color"
        const val EXTRA_IMPORTANCE = "importance"
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        
        sbn?.let { notification ->
            processNotification(notification)
        }
    }

    private fun processNotification(sbn: StatusBarNotification) {
        val notification = sbn.notification
        val packageName = sbn.packageName
        
        // Skip our own notifications
        if (packageName == this.packageName) return
        
        // Extract notification details
        val extras = notification.extras
        val title = extras.getCharSequence(NotificationCompat.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(NotificationCompat.EXTRA_TEXT)?.toString() ?: ""
        
        // Extract color from notification
        val extractedColor = extractNotificationColor(notification, packageName)
        
        // Get notification importance/priority
        val importance = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            sbn.notification.priority
        } else {
            Notification.PRIORITY_DEFAULT
        }
        
        Log.d(TAG, "Notification from $packageName: $title - Color: ${Integer.toHexString(extractedColor)}")
        
        // Send broadcast to EdgeLightingService
        sendNotificationBroadcast(packageName, title, text, extractedColor, importance)
    }

    private fun extractNotificationColor(notification: Notification, packageName: String): Int {
        // Try to get color from notification
        var color = notification.color
        
        // If no color set, try to extract from large icon
        if (color == Notification.COLOR_DEFAULT) {
            color = extractColorFromIcon(notification)
        }
        
        // If still no color, use package-specific default colors
        if (color == Notification.COLOR_DEFAULT) {
            color = getDefaultColorForPackage(packageName)
        }
        
        return color
    }

    private fun extractColorFromIcon(notification: Notification): Int {
        try {
            val largeIcon = notification.getLargeIcon()
            if (largeIcon != null) {
                val drawable = largeIcon.loadDrawable(this)
                if (drawable is BitmapDrawable) {
                    val bitmap = drawable.bitmap
                    return extractDominantColor(bitmap)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to extract color from icon", e)
        }
        return Notification.COLOR_DEFAULT
    }

    private fun extractDominantColor(bitmap: Bitmap): Int {
        return try {
            val palette = Palette.from(bitmap).generate()
            palette.dominantSwatch?.rgb 
                ?: palette.vibrantSwatch?.rgb 
                ?: palette.darkVibrantSwatch?.rgb 
                ?: Notification.COLOR_DEFAULT
        } catch (e: Exception) {
            Log.w(TAG, "Failed to generate palette", e)
            Notification.COLOR_DEFAULT
        }
    }

    private fun getDefaultColorForPackage(packageName: String): Int {
        return when {
            packageName.contains("whatsapp") -> 0xFF25D366.toInt()
            packageName.contains("telegram") -> 0xFF0088CC.toInt()
            packageName.contains("facebook") -> 0xFF1877F2.toInt()
            packageName.contains("instagram") -> 0xFFE4405F.toInt()
            packageName.contains("twitter") -> 0xFF1DA1F2.toInt()
            packageName.contains("gmail") -> 0xFFEA4335.toInt()
            packageName.contains("outlook") -> 0xFF0078D4.toInt()
            packageName.contains("youtube") -> 0xFFFF0000.toInt()
            packageName.contains("spotify") -> 0xFF1DB954.toInt()
            packageName.contains("discord") -> 0xFF5865F2.toInt()
            packageName.contains("snapchat") -> 0xFFFFFC00.toInt()
            packageName.contains("tiktok") -> 0xFF000000.toInt()
            packageName.contains("linkedin") -> 0xFF0A66C2.toInt()
            packageName.contains("phone") || packageName.contains("dialer") -> 0xFF4CAF50.toInt()
            packageName.contains("sms") || packageName.contains("message") -> 0xFF2196F3.toInt()
            else -> 0xFF6200EE.toInt() // Default purple
        }
    }

    private fun sendNotificationBroadcast(
        packageName: String,
        title: String,
        text: String,
        color: Int,
        importance: Int
    ) {
        val intent = Intent(ACTION_NOTIFICATION_POSTED).apply {
            putExtra(EXTRA_PACKAGE_NAME, packageName)
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_TEXT, text)
            putExtra(EXTRA_COLOR, color)
            putExtra(EXTRA_IMPORTANCE, importance)
        }
        sendBroadcast(intent)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        // Handle notification removal if needed
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "Notification listener connected")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "Notification listener disconnected")
    }
}
