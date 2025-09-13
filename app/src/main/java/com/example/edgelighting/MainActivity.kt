package com.example.edgelighting

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {

    private lateinit var tvServiceStatus: TextView
    private lateinit var btnStartService: Button
    private lateinit var btnStopService: Button
    private lateinit var btnNotificationAccess: Button
    private lateinit var btnOverlayPermission: Button
    private lateinit var ivNotificationStatus: ImageView
    private lateinit var ivOverlayStatus: ImageView
    private lateinit var switchEnabled: SwitchMaterial
    private lateinit var btnTestBlue: Button
    private lateinit var btnTestGreen: Button
    private lateinit var btnTestRed: Button

    // Android 13+ notification permission launcher
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        updatePermissionStatus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupClickListeners()
        updatePermissionStatus()
        
        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasPostNotificationPermission()) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun onResume() {
        super.onResume()
        updatePermissionStatus()
        updateServiceStatus()
    }

    private fun initializeViews() {
        tvServiceStatus = findViewById(R.id.tvServiceStatus)
        btnStartService = findViewById(R.id.btnStartService)
        btnStopService = findViewById(R.id.btnStopService)
        btnNotificationAccess = findViewById(R.id.btnNotificationAccess)
        btnOverlayPermission = findViewById(R.id.btnOverlayPermission)
        ivNotificationStatus = findViewById(R.id.ivNotificationStatus)
        ivOverlayStatus = findViewById(R.id.ivOverlayStatus)
        switchEnabled = findViewById(R.id.switchEnabled)
        btnTestBlue = findViewById(R.id.btnTestBlue)
        btnTestGreen = findViewById(R.id.btnTestGreen)
        btnTestRed = findViewById(R.id.btnTestRed)
    }

    private fun setupClickListeners() {
        btnStartService.setOnClickListener {
            startEdgeLightingService()
        }

        btnStopService.setOnClickListener {
            stopEdgeLightingService()
        }

        btnNotificationAccess.setOnClickListener {
            requestNotificationListenerPermission()
        }

        btnOverlayPermission.setOnClickListener {
            requestOverlayPermission()
        }

        btnTestBlue.setOnClickListener {
            testEdgeLighting(Color.BLUE)
        }

        btnTestGreen.setOnClickListener {
            testEdgeLighting(Color.GREEN)
        }

        btnTestRed.setOnClickListener {
            testEdgeLighting(Color.RED)
        }

        switchEnabled.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (hasAllPermissions()) {
                    startEdgeLightingService()
                } else {
                    switchEnabled.isChecked = false
                    showPermissionRequiredMessage()
                }
            } else {
                stopEdgeLightingService()
            }
        }
    }

    private fun startEdgeLightingService() {
        if (!hasAllPermissions()) {
            showPermissionRequiredMessage()
            return
        }

        val intent = Intent(this, EdgeLightingService::class.java).apply {
            action = EdgeLightingService.ACTION_START_SERVICE
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        
        updateServiceStatus()
    }

    private fun stopEdgeLightingService() {
        val intent = Intent(this, EdgeLightingService::class.java).apply {
            action = EdgeLightingService.ACTION_STOP_SERVICE
        }
        stopService(intent)
        updateServiceStatus()
    }

    private fun testEdgeLighting(color: Int) {
        if (!hasAllPermissions()) {
            showPermissionRequiredMessage()
            return
        }

        val intent = Intent(this, EdgeLightingService::class.java).apply {
            action = EdgeLightingService.ACTION_SHOW_EDGE_LIGHTING
            putExtra(EdgeLightingService.EXTRA_COLOR, color)
            putExtra(EdgeLightingService.EXTRA_DURATION, 3000L)
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun requestNotificationListenerPermission() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        startActivity(intent)
    }

    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                data = Uri.parse("package:$packageName")
            }
            startActivity(intent)
        }
    }

    private fun hasNotificationListenerPermission(): Boolean {
        val componentName = ComponentName(this, EdgeNotificationListener::class.java)
        val enabledListeners = Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        )
        return enabledListeners?.contains(componentName.flattenToString()) == true
    }

    private fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }

    private fun hasAllPermissions(): Boolean {
        return hasNotificationListenerPermission() && 
               hasOverlayPermission() && 
               hasPostNotificationPermission()
    }

    private fun hasPostNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this, 
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun updatePermissionStatus() {
        // Update notification access status
        val hasNotificationAccess = hasNotificationListenerPermission()
        ivNotificationStatus.setImageResource(
            if (hasNotificationAccess) android.R.drawable.ic_dialog_info
            else android.R.drawable.ic_dialog_alert
        )
        ivNotificationStatus.setColorFilter(
            ContextCompat.getColor(
                this,
                if (hasNotificationAccess) android.R.color.holo_green_dark
                else android.R.color.holo_red_dark
            )
        )
        btnNotificationAccess.text = if (hasNotificationAccess) "Granted" else "Grant"
        btnNotificationAccess.isEnabled = !hasNotificationAccess

        // Update overlay permission status
        val hasOverlay = hasOverlayPermission()
        ivOverlayStatus.setImageResource(
            if (hasOverlay) android.R.drawable.ic_dialog_info
            else android.R.drawable.ic_dialog_alert
        )
        ivOverlayStatus.setColorFilter(
            ContextCompat.getColor(
                this,
                if (hasOverlay) android.R.color.holo_green_dark
                else android.R.color.holo_red_dark
            )
        )
        btnOverlayPermission.text = if (hasOverlay) "Granted" else "Grant"
        btnOverlayPermission.isEnabled = !hasOverlay

        // Update service controls
        val allPermissionsGranted = hasAllPermissions()
        btnStartService.isEnabled = allPermissionsGranted
        switchEnabled.isEnabled = allPermissionsGranted
    }

    private fun updateServiceStatus() {
        // This is a simplified check - in a real app you might want to 
        // implement a more robust service status checking mechanism
        tvServiceStatus.text = if (switchEnabled.isChecked) {
            "Service is running"
        } else {
            "Service is not running"
        }
    }

    private fun showPermissionRequiredMessage() {
        // You could show a dialog or toast here explaining why permissions are needed
        android.widget.Toast.makeText(
            this,
            "Please grant all required permissions to use edge lighting",
            android.widget.Toast.LENGTH_LONG
        ).show()
    }
}
