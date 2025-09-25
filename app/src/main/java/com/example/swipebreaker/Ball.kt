package com.example.swipebreaker

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF

class Ball(private val bitmap: Bitmap) {
    private var x: Float = 0f
    private var y: Float = 0f
    private var velocityX: Float = 0f
    private var velocityY: Float = 0f
    private val radius: Float = 20f
    private var isActive: Boolean = false
    
    companion object {
        const val SPEED = 15f
    }
    
    fun setPosition(x: Float, y: Float) {
        this.x = x
        this.y = y
    }
    
    fun setVelocity(velX: Float, velY: Float) {
        velocityX = velX * SPEED
        velocityY = velY * SPEED
        isActive = true
    }
    
    fun update() {
        if (!isActive) return
        
        x += velocityX
        y += velocityY
    }
    
    fun draw(canvas: Canvas) {
        if (!isActive) return
        
        canvas.drawBitmap(bitmap, x - radius, y - radius, null)
    }
    
    fun getRect(): RectF {
        return RectF(x - radius, y - radius, x + radius, y + radius)
    }
    
    fun isActive(): Boolean = isActive
    
    fun reset() {
        isActive = false
        velocityX = 0f
        velocityY = 0f
    }
    
    fun checkWallCollision(screenWidth: Int, screenHeight: Int): Boolean {
        if (x - radius <= 0 || x + radius >= screenWidth) {
            velocityX = -velocityX
        }
        if (y - radius <= 0) {
            velocityY = -velocityY
        }
        
        return y + radius >= screenHeight
    }
}