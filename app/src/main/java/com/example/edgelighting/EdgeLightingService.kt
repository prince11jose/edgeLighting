package com.example.edgelighting

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.core.app.NotificationCompat

class EdgeLightingService : Service() {

    companion object {
        private const val TAG = "EdgeLightingService"
        private const val FOREGROUND_NOTIFICATION_ID = 1001
        private const val NOTIFICATION_CHANNEL_ID = "edge_lighting_service"
        
        const val ACTION_START_SERVICE = "com.example.edgelighting.START_SERVICE"
        const val ACTION_STOP_SERVICE = "com.example.edgelighting.STOP_SERVICE"
        const val ACTION_SHOW_EDGE_LIGHTING = "com.example.edgelighting.SHOW_EDGE_LIGHTING"
        
        const val EXTRA_COLOR = "color"
        const val EXTRA_DURATION = "duration"
    }

    private var windowManager: WindowManager? = null
    private var edgeLightingView: EdgeLightingView? = null
    private var isOverlayShowing = false
    
    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                EdgeNotificationListener.ACTION_NOTIFICATION_POSTED -> {
                    val color = intent.getIntExtra(EdgeNotificationListener.EXTRA_COLOR, 0xFF6200EE.toInt())
                    val packageName = intent.getStringExtra(EdgeNotificationListener.EXTRA_PACKAGE_NAME) ?: ""
                    val importance = intent.getIntExtra(EdgeNotificationListener.EXTRA_IMPORTANCE, 0)
                    
                    Log.d(TAG, "Received notification from $packageName with color ${Integer.toHexString(color)}")
                    
                    // Only show edge lighting for important notifications
                    if (importance >= Notification.PRIORITY_DEFAULT) {
                        showEdgeLighting(color)
                    }
                }
                ACTION_SHOW_EDGE_LIGHTING -> {
                    val color = intent.getIntExtra(EXTRA_COLOR, 0xFF6200EE.toInt())
                    val duration = intent.getLongExtra(EXTRA_DURATION, 2000L)
                    showEdgeLighting(color, duration)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "EdgeLightingService created")
        
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        // Register broadcast receiver
        val filter = IntentFilter().apply {
            addAction(EdgeNotificationListener.ACTION_NOTIFICATION_POSTED)
            addAction(ACTION_SHOW_EDGE_LIGHTING)
        }
        registerReceiver(notificationReceiver, filter)
        
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SERVICE -> {
                startForegroundService()
                Log.d(TAG, "Service started")
            }
            ACTION_STOP_SERVICE -> {
                stopSelf()
                Log.d(TAG, "Service stopping")
            }
            ACTION_SHOW_EDGE_LIGHTING -> {
                val color = intent.getIntExtra(EXTRA_COLOR, 0xFF6200EE.toInt())
                val duration = intent.getLongExtra(EXTRA_DURATION, 2000L)
                showEdgeLighting(color, duration)
            }
        }
        
        return START_STICKY
    }

    private fun startForegroundService() {
        val notification = createForegroundNotification()
        startForeground(FOREGROUND_NOTIFICATION_ID, notification)
    }

    private fun createForegroundNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Edge Lighting Active")
            .setContentText("Monitoring notifications for edge lighting effects")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Edge Lighting Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel for Edge Lighting service notifications"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showEdgeLighting(color: Int, duration: Long = 2000L) {
        if (!canDrawOverlays()) {
            Log.w(TAG, "Cannot draw overlays - permission not granted")
            return
        }

        try {
            if (!isOverlayShowing) {
                createOverlay()
            }
            
            edgeLightingView?.startEdgeLighting(color, duration)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error showing edge lighting", e)
        }
    }

    private fun createOverlay() {
        if (edgeLightingView != null) return
        
        edgeLightingView = EdgeLightingView(this)
        
        val layoutParams = WindowManager.LayoutParams().apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
            
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }
            
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            
            format = PixelFormat.TRANSLUCENT
            gravity = Gravity.TOP or Gravity.START
        }
        
        try {
            windowManager?.addView(edgeLightingView, layoutParams)
            isOverlayShowing = true
            Log.d(TAG, "Overlay created successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create overlay", e)
            edgeLightingView = null
        }
    }

    private fun removeOverlay() {
        edgeLightingView?.let { view ->
            try {
                windowManager?.removeView(view)
                Log.d(TAG, "Overlay removed")
            } catch (e: Exception) {
                Log.e(TAG, "Error removing overlay", e)
            } finally {
                edgeLightingView = null
                isOverlayShowing = false
            }
        }
    }

    private fun canDrawOverlays(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "EdgeLightingService destroyed")
        
        try {
            unregisterReceiver(notificationReceiver)
        } catch (e: Exception) {
            Log.w(TAG, "Error unregistering receiver", e)
        }
        
        removeOverlay()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // Public methods for external control
    fun testEdgeLighting(color: Int = 0xFF6200EE.toInt()) {
        showEdgeLighting(color, 3000L)
    }
    
    fun isServiceRunning(): Boolean {
        return isOverlayShowing
    }
}
