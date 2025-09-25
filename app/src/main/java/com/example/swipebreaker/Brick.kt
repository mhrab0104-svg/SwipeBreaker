package com.example.swipebreaker

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF

class Brick(private val bitmap: Bitmap) {
    private var x: Float = 0f
    private var y: Float = 0f
    private val width: Float = 80f
    private val height: Float = 40f
    private var durability: Int = 3
    
    fun setPosition(x: Float, y: Float) {
        this.x = x
        this.y = y
    }
    
    fun getRect(): RectF {
        return RectF(x, y, x + width, y + height)
    }
    
    fun hit(): Boolean {
        durability--
        return durability <= 0
    }
    
    fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, x, y, null)
    }
    
    fun moveDown(distance: Float) {
        y += distance
    }
    
    fun getY(): Float = y
    fun getHeight(): Float = height
}