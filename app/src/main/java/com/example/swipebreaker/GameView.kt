package com.example.swipebreaker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2
import kotlin.random.Random

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    private lateinit var ballBitmap: Bitmap
    private lateinit var brickBitmap: Bitmap
    private val ball = Ball(ballBitmap)
    private val bricks = mutableListOf<Brick>()
    private var score = 0
    private var gameOver = false
    private var startX = 0f
    private var startY = 0f
    private var isDrawingLine = false
    private val linePaint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }
    
    private val brickRows = 4
    private val brickCols = 6
    private val brickSpacing = 10f
    
    init {
        createBitmaps()
        startNewGame()
    }
    
    private fun createBitmaps() {
        ballBitmap = Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888).apply {
            val canvas = Canvas(this)
            val paint = Paint().apply {
                color = Color.RED
                style = Paint.Style.FILL
            }
            canvas.drawCircle(20f, 20f, 20f, paint)
        }
        
        brickBitmap = Bitmap.createBitmap(80, 40, Bitmap.Config.ARGB_8888).apply {
            val canvas = Canvas(this)
            val paint = Paint().apply {
                color = Color.BLUE
                style = Paint.Style.FILL
            }
            canvas.drawRect(0f, 0f, 80f, 40f, paint)
        }
        
        ball.bitmap = ballBitmap
    }
    
    private fun startNewGame() {
        score = 0
        gameOver = false
        bricks.clear()
        createBricks()
        resetBall()
    }
    
    private fun createBricks() {
        val totalWidth = width - (brickSpacing * (brickCols + 1))
        val brickWidth = totalWidth / brickCols
        
        for (row in 0 until brickRows) {
            for (col in 0 until brickCols) {
                val brick = Brick(brickBitmap)
                val x = brickSpacing + col * (brickWidth + brickSpacing)
                val y = brickSpacing + row * (60f + brickSpacing)
                brick.setPosition(x, y)
                bricks.add(brick)
            }
        }
    }
    
    private fun resetBall() {
        ball.setPosition(width / 2f, height - 100f)
        ball.reset()
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        canvas.drawColor(Color.BLACK)
        
        // Draw aiming line
        if (isDrawingLine && !ball.isActive()) {
            canvas.drawLine(startX, startY, ball.getRect().centerX(), ball.getRect().centerY(), linePaint)
        }
        
        // Draw ball
        ball.draw(canvas)
        
        // Draw bricks
        bricks.forEach { it.draw(canvas) }
        
        // Update game state
        if (!gameOver) {
            updateGame()
            invalidate()
        }
    }
    
    private fun updateGame() {
        if (ball.isActive()) {
            ball.update()
            
            // Check wall collisions
            if (ball.checkWallCollision(width, height)) {
                gameOver = true
                return
            }
            
            // Check brick collisions
            val iterator = bricks.iterator()
            var brickHit = false
            
            while (iterator.hasNext()) {
                val brick = iterator.next()
                if (RectF.intersects(ball.getRect(), brick.getRect())) {
                    if (brick.hit()) {
                        iterator.remove()
                        score += 10
                    }
                    ball.velocityY = -ball.velocityY
                    brickHit = true
                    break
                }
            }
            
            // If ball hit a brick and no bricks left, add new row
            if (brickHit && bricks.none { it.getY() <= height / 2f }) {
                addNewBrickRow()
            }
            
            // Check if bricks reached bottom
            if (bricks.any { it.getY() + it.getHeight() >= height }) {
                gameOver = true
            }
        }
    }
    
    private fun addNewBrickRow() {
        bricks.forEach { it.moveDown(60f) }
        
        for (col in 0 until brickCols) {
            val brick = Brick(brickBitmap)
            val x = brickSpacing + col * ((width - brickSpacing * (brickCols + 1)) / brickCols + brickSpacing)
            val y = brickSpacing
            brick.setPosition(x, y)
            bricks.add(brick)
        }
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (gameOver) {
                    startNewGame()
                    invalidate()
                    return true
                }
                
                if (!ball.isActive()) {
                    startX = event.x
                    startY = event.y
                    isDrawingLine = true
                }
            }
            
            MotionEvent.ACTION_UP -> {
                if (isDrawingLine && !ball.isActive()) {
                    val endX = ball.getRect().centerX()
                    val endY = ball.getRect().centerY()
                    val dx = startX - endX
                    val dy = startY - endY
                    val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
                    
                    if (distance > 50f) {
                        val velX = dx / distance
                        val velY = dy / distance
                        ball.setVelocity(velX, velY)
                    }
                    
                    isDrawingLine = false
                }
            }
        }
        return true
    }
    
    fun getScore(): Int = score
    fun isGameOver(): Boolean = gameOver
}