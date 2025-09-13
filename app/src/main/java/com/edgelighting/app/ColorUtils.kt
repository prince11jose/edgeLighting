package com.edgelighting.app

import android.graphics.Bitmap
import android.graphics.Color
import androidx.palette.graphics.Palette
import kotlin.math.*

object ColorUtils {

    /**
     * Extract the most vibrant color from a bitmap
     */
    fun extractVibrantColor(bitmap: Bitmap, fallbackColor: Int = Color.BLUE): Int {
        return try {
            val palette = Palette.from(bitmap).generate()
            palette.vibrantSwatch?.rgb
                ?: palette.lightVibrantSwatch?.rgb
                ?: palette.darkVibrantSwatch?.rgb
                ?: palette.dominantSwatch?.rgb
                ?: fallbackColor
        } catch (e: Exception) {
            fallbackColor
        }
    }

    /**
     * Get complementary colors for multicolor effects
     */
    fun getComplementaryColors(baseColor: Int): List<Int> {
        val hsv = FloatArray(3)
        Color.colorToHSV(baseColor, hsv)
        
        val colors = mutableListOf<Int>()
        
        // Original color
        colors.add(baseColor)
        
        // Complementary color (opposite hue)
        val complementaryHsv = hsv.clone()
        complementaryHsv[0] = (complementaryHsv[0] + 180) % 360
        colors.add(Color.HSVToColor(complementaryHsv))
        
        // Triadic colors (120 degrees apart)
        val triadic1Hsv = hsv.clone()
        triadic1Hsv[0] = (triadic1Hsv[0] + 120) % 360
        colors.add(Color.HSVToColor(triadic1Hsv))
        
        val triadic2Hsv = hsv.clone()
        triadic2Hsv[0] = (triadic2Hsv[0] + 240) % 360
        colors.add(Color.HSVToColor(triadic2Hsv))
        
        return colors
    }

    /**
     * Create a gradient between two colors
     */
    fun createGradient(startColor: Int, endColor: Int, steps: Int): List<Int> {
        val colors = mutableListOf<Int>()
        
        val startA = Color.alpha(startColor)
        val startR = Color.red(startColor)
        val startG = Color.green(startColor)
        val startB = Color.blue(startColor)
        
        val endA = Color.alpha(endColor)
        val endR = Color.red(endColor)
        val endG = Color.green(endColor)
        val endB = Color.blue(endColor)
        
        for (i in 0 until steps) {
            val ratio = i.toFloat() / (steps - 1)
            
            val a = (startA + ratio * (endA - startA)).toInt()
            val r = (startR + ratio * (endR - startR)).toInt()
            val g = (startG + ratio * (endG - startG)).toInt()
            val b = (startB + ratio * (endB - startB)).toInt()
            
            colors.add(Color.argb(a, r, g, b))
        }
        
        return colors
    }

    /**
     * Adjust color brightness
     */
    fun adjustBrightness(color: Int, factor: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] = (hsv[2] * factor).coerceIn(0f, 1f)
        return Color.HSVToColor(Color.alpha(color), hsv)
    }

    /**
     * Adjust color saturation
     */
    fun adjustSaturation(color: Int, factor: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[1] = (hsv[1] * factor).coerceIn(0f, 1f)
        return Color.HSVToColor(Color.alpha(color), hsv)
    }

    /**
     * Get package-specific colors for better app identification
     */
    fun getPackageColor(packageName: String): Int {
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
            packageName.contains("calendar") -> 0xFFFF9800.toInt()
            packageName.contains("clock") || packageName.contains("alarm") -> 0xFF9C27B0.toInt()
            packageName.contains("camera") -> 0xFF607D8B.toInt()
            packageName.contains("gallery") || packageName.contains("photo") -> 0xFFE91E63.toInt()
            packageName.contains("music") -> 0xFF673AB7.toInt()
            packageName.contains("video") -> 0xFF795548.toInt()
            packageName.contains("maps") -> 0xFF4CAF50.toInt()
            packageName.contains("uber") -> 0xFF000000.toInt()
            packageName.contains("lyft") -> 0xFFFF00BF.toInt()
            packageName.contains("netflix") -> 0xFFE50914.toInt()
            packageName.contains("amazon") -> 0xFFFF9900.toInt()
            packageName.contains("paypal") -> 0xFF003087.toInt()
            packageName.contains("venmo") -> 0xFF3D95CE.toInt()
            packageName.contains("slack") -> 0xFF4A154B.toInt()
            packageName.contains("teams") -> 0xFF6264A7.toInt()
            packageName.contains("zoom") -> 0xFF2D8CFF.toInt()
            packageName.contains("skype") -> 0xFF00AFF0.toInt()
            else -> generateColorFromPackageName(packageName)
        }
    }

    /**
     * Generate a consistent color based on package name hash
     */
    private fun generateColorFromPackageName(packageName: String): Int {
        val hash = packageName.hashCode()
        val hue = (hash and 0xFF) * 360f / 255f
        val saturation = 0.7f + (((hash shr 8) and 0xFF) / 255f) * 0.3f
        val value = 0.8f + (((hash shr 16) and 0xFF) / 255f) * 0.2f
        
        return Color.HSVToColor(floatArrayOf(hue, saturation, value))
    }

    /**
     * Check if a color is considered "dark"
     */
    fun isDarkColor(color: Int): Boolean {
        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness >= 0.5
    }

    /**
     * Get a contrasting color for text/icons
     */
    fun getContrastingColor(backgroundColor: Int): Int {
        return if (isDarkColor(backgroundColor)) Color.WHITE else Color.BLACK
    }

    /**
     * Blend two colors
     */
    fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
        val inverseRatio = 1f - ratio
        
        val a = (Color.alpha(color1) * inverseRatio + Color.alpha(color2) * ratio).toInt()
        val r = (Color.red(color1) * inverseRatio + Color.red(color2) * ratio).toInt()
        val g = (Color.green(color1) * inverseRatio + Color.green(color2) * ratio).toInt()
        val b = (Color.blue(color1) * inverseRatio + Color.blue(color2) * ratio).toInt()
        
        return Color.argb(a, r, g, b)
    }

    /**
     * Create a rainbow effect with multiple colors
     */
    fun createRainbowColors(count: Int): List<Int> {
        val colors = mutableListOf<Int>()
        for (i in 0 until count) {
            val hue = (i * 360f / count) % 360f
            colors.add(Color.HSVToColor(floatArrayOf(hue, 1f, 1f)))
        }
        return colors
    }

    /**
     * Create pulsing alpha values for animation
     */
    fun getPulseAlpha(time: Long, period: Long = 1000L): Int {
        val progress = (time % period).toFloat() / period
        val alpha = (sin(progress * 2 * PI) * 0.5 + 0.5).toFloat()
        return (alpha * 255).toInt().coerceIn(0, 255)
    }

    /**
     * Create wave effect for traveling lights
     */
    fun getWavePosition(time: Long, period: Long = 2000L): Float {
        return ((time % period).toFloat() / period)
    }
}
