package com.example.edgelighting

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.*

class EdgeLightingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val gradientPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private var currentColor: Int = Color.BLUE
    private var strokeWidth: Float = 8f
    private var cornerRadius: Float = 40f
    private var animationProgress: Float = 0f
    private var isAnimating = false
    
    private var animator: ValueAnimator? = null
    private val animationDuration = 2000L
    
    // Animation parameters
    private var lightPosition: Float = 0f
    private var lightSpeed: Float = 1f
    private var fadeAlpha: Int = 255

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    fun startEdgeLighting(color: Int, duration: Long = animationDuration) {
        if (isAnimating) {
            stopEdgeLighting()
        }
        
        currentColor = color
        isAnimating = true
        
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            this.duration = duration
            interpolator = DecelerateInterpolator()
            
            addUpdateListener { animation ->
                animationProgress = animation.animatedValue as Float
                lightPosition = animationProgress
                
                // Create pulsing effect
                val pulse = sin(animationProgress * PI * 4).toFloat()
                fadeAlpha = (155 + 100 * pulse).toInt().coerceIn(55, 255)
                
                invalidate()
            }
            
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    isAnimating = false
                    fadeAlpha = 0
                    invalidate()
                }
            })
            
            start()
        }
    }

    fun stopEdgeLighting() {
        animator?.cancel()
        animator = null
        isAnimating = false
        fadeAlpha = 0
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        if (!isAnimating || fadeAlpha <= 0) return
        
        val width = width.toFloat()
        val height = height.toFloat()
        
        if (width <= 0 || height <= 0) return
        
        // Calculate stroke width based on screen size
        val dynamicStrokeWidth = strokeWidth * (width / 1080f).coerceAtLeast(1f)
        paint.strokeWidth = dynamicStrokeWidth
        gradientPaint.strokeWidth = dynamicStrokeWidth
        
        // Draw the edge lighting effect
        drawEdgeLighting(canvas, width, height)
    }

    private fun drawEdgeLighting(canvas: Canvas, width: Float, height: Float) {
        val halfStroke = paint.strokeWidth / 2f
        val rect = RectF(
            halfStroke,
            halfStroke,
            width - halfStroke,
            height - halfStroke
        )
        
        // Create gradient colors
        val colors = createGradientColors(currentColor, fadeAlpha)
        val positions = floatArrayOf(0f, 0.3f, 0.7f, 1f)
        
        // Calculate the perimeter
        val perimeter = 2 * (rect.width() + rect.height() - 4 * cornerRadius) + 2 * PI.toFloat() * cornerRadius
        
        // Create traveling light effect
        val lightLength = perimeter * 0.15f // Length of the light trail
        val lightStart = (lightPosition * perimeter) % perimeter
        val lightEnd = (lightStart + lightLength) % perimeter
        
        // Draw multiple segments for smooth gradient effect
        drawEdgeSegments(canvas, rect, colors, positions, lightStart, lightEnd, perimeter)
    }

    private fun createGradientColors(baseColor: Int, alpha: Int): IntArray {
        val transparent = Color.argb(0, Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor))
        val semiTransparent = Color.argb(alpha / 3, Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor))
        val fullColor = Color.argb(alpha, Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor))
        
        return intArrayOf(transparent, semiTransparent, fullColor, semiTransparent)
    }

    private fun drawEdgeSegments(
        canvas: Canvas,
        rect: RectF,
        colors: IntArray,
        positions: FloatArray,
        lightStart: Float,
        lightEnd: Float,
        perimeter: Float
    ) {
        val path = Path()
        
        // Create rounded rectangle path
        path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW)
        
        // Use PathMeasure to draw gradient along the path
        val pathMeasure = PathMeasure(path, false)
        val pathLength = pathMeasure.length
        
        // Draw the light effect
        val segments = 100
        val segmentLength = pathLength / segments
        
        for (i in 0 until segments) {
            val segmentStart = i * segmentLength
            val segmentEnd = (i + 1) * segmentLength
            
            // Convert path position to perimeter position
            val perimeterStart = (segmentStart / pathLength) * perimeter
            val perimeterEnd = (segmentEnd / pathLength) * perimeter
            
            // Check if this segment should be lit
            val alpha = calculateSegmentAlpha(perimeterStart, perimeterEnd, lightStart, lightEnd, perimeter)
            
            if (alpha > 0) {
                val segmentPath = Path()
                pathMeasure.getSegment(segmentStart, segmentEnd, segmentPath, true)
                
                paint.color = Color.argb(
                    (alpha * fadeAlpha / 255f).toInt(),
                    Color.red(currentColor),
                    Color.green(currentColor),
                    Color.blue(currentColor)
                )
                
                canvas.drawPath(segmentPath, paint)
            }
        }
    }

    private fun calculateSegmentAlpha(
        segmentStart: Float,
        segmentEnd: Float,
        lightStart: Float,
        lightEnd: Float,
        perimeter: Float
    ): Float {
        // Handle wrapping around the perimeter
        val normalizedLightStart = lightStart % perimeter
        val normalizedLightEnd = lightEnd % perimeter
        
        return when {
            normalizedLightStart <= normalizedLightEnd -> {
                // Light doesn't wrap around
                if (segmentEnd >= normalizedLightStart && segmentStart <= normalizedLightEnd) {
                    val overlap = minOf(segmentEnd, normalizedLightEnd) - maxOf(segmentStart, normalizedLightStart)
                    val segmentLength = segmentEnd - segmentStart
                    (overlap / segmentLength).coerceIn(0f, 1f)
                } else 0f
            }
            else -> {
                // Light wraps around
                val beforeWrap = if (segmentEnd >= normalizedLightStart) {
                    minOf(segmentEnd, perimeter) - maxOf(segmentStart, normalizedLightStart)
                } else 0f
                
                val afterWrap = if (segmentStart <= normalizedLightEnd) {
                    minOf(segmentEnd, normalizedLightEnd) - maxOf(segmentStart, 0f)
                } else 0f
                
                val totalOverlap = beforeWrap + afterWrap
                val segmentLength = segmentEnd - segmentStart
                (totalOverlap / segmentLength).coerceIn(0f, 1f)
            }
        }
    }

    fun setStrokeWidth(width: Float) {
        strokeWidth = width
        invalidate()
    }

    fun setCornerRadius(radius: Float) {
        cornerRadius = radius
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopEdgeLighting()
    }
}
