-keep class com.example.edgelighting.** { *; }
-keepclassmembers class com.example.edgelighting.** { *; }

# Keep notification listener service
-keep class * extends android.service.notification.NotificationListenerService {
    public <init>(...);
    public void onNotificationPosted(...);
    public void onNotificationRemoved(...);
}

# Keep service classes
-keep class * extends android.app.Service {
    public <init>(...);
}

# Keep Palette library
-keep class androidx.palette.graphics.** { *; }
-dontwarn androidx.palette.graphics.**
