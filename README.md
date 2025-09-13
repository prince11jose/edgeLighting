# 🌈 Edge Lighting - Multicolour Notification Effects

A beautiful Android app that creates stunning multicolour edge lighting effects for all types of notifications. Transform your phone's notification experience with dynamic, colorful light animations around your screen edges.

![Android](https://img.shields.io/badge/Android-5.0%2B-green.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-blue.svg)
![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

## ✨ Features

- **🎨 Smart Color Detection**: Automatically extracts vibrant colors from notification icons
- **⚡ Real-time Effects**: Instant edge lighting for all incoming notifications
- **🌟 Beautiful Animations**: Smooth traveling light effects with gradient transitions
- **📱 Universal Support**: Works with notifications from any app
- **🎯 App-specific Colors**: Predefined colors for 30+ popular apps (WhatsApp, Instagram, Gmail, etc.)
- **🔒 Privacy Focused**: No data collection, all processing happens locally
- **⚙️ Easy Setup**: Simple permission management and one-tap activation
- **🔧 Android 14 Ready**: Full compatibility with latest Android versions

## 🚀 Quick Start

### **📱 Download & Install (Recommended)**
1. Go to **[GitHub Actions](../../actions)** → **Build Edge Lighting APK** → **Latest Run**
2. Download **edge-lighting-debug-apk** from artifacts
3. Install APK on Android device (enable "Unknown Sources" in Settings)
4. Grant required permissions when prompted

### **🎉 Alternative: GitHub Releases**
- Check **[Releases](../../releases)** for stable versions
- Download APK directly from release assets

## 🔧 Build from Source

### **GitHub Actions (Automatic - Recommended)**
```bash
# Fork this repository
# GitHub Actions automatically builds APK on x86_64 runners
# No setup required - just download from Actions artifacts
```

### **Android Studio (Local Development)**
```bash
# Clone repository
git clone https://github.com/yourusername/edge-lighting.git
cd edge-lighting

# Open in Android Studio
# Build → Generate Signed Bundle / APK
```

### **Command Line Build (x86_64 Systems)**
```bash
# Prerequisites: Android SDK, JDK 17
./gradlew assembleDebug
# APK location: app/build/outputs/apk/debug/app-debug.apk
```

## 📱 Setup Instructions

### **1. Installation**
- Enable "Unknown Sources" in Android Settings → Security
- Install the downloaded APK file
- Open the Edge Lighting app

### **2. Grant Permissions**
The app requires two essential permissions:

#### **Notification Listener Access**
- Settings → Apps → Special Access → Notification Access
- Find "Edge Lighting" and toggle ON
- Or use the in-app "Grant Notification Access" button

#### **Display Over Other Apps**
- Settings → Apps → Special Access → Display over other apps  
- Find "Edge Lighting" and toggle ON
- Or use the in-app "Grant Overlay Permission" button

### **3. Start the Service**
- Toggle "Enable Edge Lighting" in the app
- Service will run in background monitoring notifications

### **4. Test the Setup**
- Use the color test buttons (Red, Green, Blue) in the app
- Send yourself a notification to see edge lighting in action

## 🎨 How It Works

### **Smart Color Extraction**
- Extracts dominant colors from notification icons using Android's Palette API
- Falls back to predefined colors for popular apps
- Supports 30+ app-specific color mappings

### **App Color Mappings**
- **Social**: WhatsApp (Green), Instagram (Pink), Facebook (Blue)
- **Communication**: Gmail (Red), Telegram (Blue), Discord (Purple)  
- **Entertainment**: YouTube (Red), Spotify (Green), Netflix (Red)
- **And many more...**

### **Animation System**
- Smooth traveling light effects around screen edges
- Configurable speed and duration
- Optimized for performance and battery life

## 🔧 Technical Details

### **Android Compatibility**
- **Minimum**: Android 5.0 (API 21)
- **Target**: Android 14 (API 34)
- **Tested on**: Android 5.0 to Android 14
- **Architecture**: ARM64, x86_64

### **Permissions Required**
- `SYSTEM_ALERT_WINDOW` - For overlay drawing
- `BIND_NOTIFICATION_LISTENER_SERVICE` - For notification access
- `FOREGROUND_SERVICE` - For background operation
- `POST_NOTIFICATIONS` - For Android 13+ notification handling

### **Build Configuration**
- **Language**: Kotlin 1.9.22
- **Gradle**: 8.2
- **Android Gradle Plugin**: 8.2.1
- **Target SDK**: 34 (Android 14)
- **Build Tools**: 34.0.0

## 🚀 GitHub Actions CI/CD

This repository includes automated APK building:

### **Automatic Triggers**
- ✅ Push to main/master/develop branches
- ✅ Pull requests
- ✅ Manual workflow dispatch

### **Manual Build**
1. Go to **Actions** tab
2. Select **"🌈 Build Edge Lighting APK"**
3. Click **"Run workflow"**
4. Choose **Debug** or **Release**
5. Download APK from **Artifacts**

### **Release Creation**
```bash
# Create and push a tag
git tag v1.0.0
git push origin v1.0.0
# Release with APKs will be created automatically
```

## 📂 Project Structure

```
edgeLighting/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/edgelighting/
│   │   │   ├── MainActivity.kt           # Main UI & permissions
│   │   │   ├── EdgeNotificationListener.kt  # Notification service
│   │   │   ├── EdgeLightingService.kt    # Foreground service
│   │   │   ├── EdgeLightingView.kt       # Custom overlay view
│   │   │   └── ColorUtils.kt             # Color processing
│   │   ├── res/                          # Android resources
│   │   └── AndroidManifest.xml           # App configuration
│   └── build.gradle                      # Module configuration
├── .github/workflows/                    # CI/CD workflows
├── gradle/                               # Gradle wrapper
└── build.gradle                          # Project configuration
```

## 🐛 Troubleshooting

### **Edge lighting not working?**
- ✅ Check notification access permission
- ✅ Check overlay permission  
- ✅ Ensure service is running (check notification tray)
- ✅ Test with color buttons in app

### **Build issues on ARM64?**
- ✅ Use GitHub Actions (builds on x86_64)
- ✅ Use Android Studio (handles architecture automatically)
- ✅ Transfer project to x86_64 system for command-line builds

### **App crashes or permissions not working?**
- ✅ Ensure Android 5.0+ device
- ✅ Grant all required permissions
- ✅ Restart app after granting permissions

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Android Palette API for color extraction
- Material Design for UI components
- GitHub Actions for automated builds

---

**🌈 Transform your notifications with beautiful edge lighting!** ✨

*Made with ❤️ for the Android community*

### Testing

Use the built-in test buttons to verify the edge lighting works:
- **Blue**: Test with blue edge lighting
- **Green**: Test with green edge lighting  
- **Red**: Test with red edge lighting

## 📁 Project Structure

```
app/
├── src/main/
│   ├── java/com/example/edgelighting/
│   │   ├── MainActivity.kt              # Main app interface
│   │   ├── EdgeNotificationListener.kt  # Notification interceptor
│   │   ├── EdgeLightingService.kt       # Background service
│   │   ├── EdgeLightingView.kt          # Custom view for animations
│   │   └── ColorUtils.kt                # Color manipulation utilities
│   ├── res/
│   │   ├── layout/
│   │   │   └── activity_main.xml        # Main activity layout
│   │   ├── values/
│   │   │   ├── strings.xml              # App strings
│   │   │   └── themes.xml               # App themes
│   │   └── mipmap/                      # App icons
│   └── AndroidManifest.xml             # App permissions and components
├── build.gradle                        # Module build configuration
└── proguard-rules.pro                  # ProGuard rules
```

## 🎨 How It Works

### 1. Notification Interception
The `EdgeNotificationListener` service monitors all incoming notifications:
- Extracts notification metadata (title, text, package name)
- Analyzes notification icons for dominant colors
- Falls back to app-specific predefined colors

### 2. Color Processing
The `ColorUtils` class provides advanced color manipulation:
- Palette-based color extraction from bitmaps
- Predefined colors for 30+ popular apps
- Dynamic color generation based on package names
- Gradient creation and color blending

### 3. Visual Effects
The `EdgeLightingView` creates stunning animations:
- Hardware-accelerated rendering
- Smooth traveling light effects around screen edges
- Gradient transitions and pulsing effects
- Adaptive sizing for different screen sizes

### 4. Service Management
The `EdgeLightingService` coordinates everything:
- Manages system overlay permissions
- Handles notification broadcasts
- Controls animation lifecycle
- Provides foreground service for reliability

## 🛠️ Customization

### Adding New App Colors

Edit `ColorUtils.kt` to add colors for specific apps:

```kotlin
fun getPackageColor(packageName: String): Int {
    return when {
        packageName.contains("yourapp") -> 0xFFYOURCOLOR.toInt()
        // ... existing mappings
        else -> generateColorFromPackageName(packageName)
    }
}
```

### Modifying Animation Parameters

Adjust animation properties in `EdgeLightingView.kt`:

```kotlin
private val animationDuration = 2000L  // Animation duration in milliseconds
private var strokeWidth: Float = 8f    // Edge lighting thickness
private var cornerRadius: Float = 40f  // Corner roundness
```

### Customizing Notification Filtering

Modify notification processing in `EdgeNotificationListener.kt`:

```kotlin
// Filter by notification importance
if (importance >= Notification.PRIORITY_DEFAULT) {
    showEdgeLighting(color)
}

// Filter by package name
if (!shouldIgnorePackage(packageName)) {
    showEdgeLighting(color)
}
```

## 🔒 Permissions

The app requires these permissions:

- **`SYSTEM_ALERT_WINDOW`**: Draw overlay on top of other apps
- **`BIND_NOTIFICATION_LISTENER_SERVICE`**: Access notification content
- **`FOREGROUND_SERVICE`**: Run background service
- **`WAKE_LOCK`**: Keep device awake during animations

## 🐛 Troubleshooting

### Edge Lighting Not Showing

1. **Check permissions**: Ensure both notification access and overlay permissions are granted
2. **Verify service**: Check if the service is running in the app
3. **Test manually**: Use the test buttons to verify functionality
4. **Check device compatibility**: Ensure Android 7.0+ and sufficient RAM

### Poor Performance

1. **Reduce animation duration**: Lower values in `EdgeLightingView.kt`
2. **Disable hardware acceleration**: Add to manifest if needed
3. **Check memory usage**: Monitor for memory leaks in service

### Notifications Not Detected

1. **Verify notification access**: Re-grant permission if needed
2. **Check app filtering**: Ensure the app isn't being filtered out
3. **Restart service**: Stop and start the edge lighting service

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Android Palette Library for color extraction
- Material Design 3 for UI components
- The Android community for inspiration and support

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/yourusername/edge-lighting/issues)
- **Discussions**: [GitHub Discussions](https://github.com/yourusername/edge-lighting/discussions)
- **Email**: your.email@example.com

---

**Made with ❤️ for the Android community**
