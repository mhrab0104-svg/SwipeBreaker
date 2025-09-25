package com.example.swipebreaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    
    private lateinit var gameView: GameView
    private lateinit var scoreText: TextView
    private lateinit var gameOverText: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        gameView = findViewById(R.id.gameView)
        scoreText = findViewById(R.id.scoreText)
        gameOverText = findViewById(R.id.gameOverText)
        
        startGameLoop()
    }
    
    private fun startGameLoop() {
        Thread {
            while (true) {
                Thread.sleep(16) // ~60 FPS
                
                runOnUiThread {
                    scoreText.text = "Score: ${gameView.getScore()}"
                    
                    if (gameView.isGameOver()) {
                        gameOverText.visibility = TextView.VISIBLE
                    } else {
                        gameOverText.visibility = TextView.GONE
                    }
                }
            }
        }.start()
    }
    
    override fun onPause() {
        super.onPause()
        // Here you might want to pause the game
    }
    
    override fun onResume() {
        super.onResume()
        // Here you might want to resume the game
    }
}