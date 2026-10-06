package com.example.gameproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.ComponentActivity

var started: Boolean = false

class MainActivity : ComponentActivity() {

    var timeCopy: Long = 0

    lateinit var sharedPreferences: SharedPreferences

    // Clear setting preferences only the first time the app is started.
    override fun onStart() {
        super.onStart()

        if (!started) {
            sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().commit()
            started = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val time = intent.getLongExtra("time", 40000)
        this.timeCopy = time

        // Spin character images when clicked.
        val imgSpiderman = findViewById<ImageView>(R.id.spiderman)
        imgSpiderman.setOnClickListener {
            imgSpiderman.animate().apply {
                duration = 2000
                rotationYBy(360f)
            }.withEndAction {
                imgSpiderman.animate().apply {
                    duration = 10
                    rotationY(180f)
                }.start()
            }
        }

        val imgIronman = findViewById<ImageView>(R.id.ironman)
        imgIronman.setOnClickListener {
            imgIronman.animate().apply {
                duration = 2000
                y(0f)
                rotationYBy(360f)
            }.withEndAction {
                imgIronman.animate().apply {
                    duration = 10
                    rotationY(180f)
                }.start()
            }
        }

        val imgGroot = findViewById<ImageView>(R.id.groot)
        imgGroot.setOnClickListener {
            imgGroot.animate().apply {
                duration = 2000
                rotationYBy(360f)
            }.withEndAction {
                imgGroot.animate().apply {
                    duration = 10
                    rotationY(180f)
                }.start()
            }
        }

        val imgCaptAmerica = findViewById<ImageView>(R.id.captAmerica)
        imgCaptAmerica.setOnClickListener {
            imgCaptAmerica.animate().apply {
                duration = 2000
                rotationYBy(360f)
            }.withEndAction {
                imgCaptAmerica.animate().apply {
                    duration = 10
                    rotationY(180f)
                }.start()
            }
        }

        val imgDeadpool = findViewById<ImageView>(R.id.deadpool)
        imgDeadpool.setOnClickListener {
            imgDeadpool.animate().apply {
                duration = 2000
                rotationYBy(360f)
            }.withEndAction {
                imgDeadpool.animate().apply {
                    duration = 10
                    rotationY(180f)
                }.start()
            }
        }

        val imgHulk = findViewById<ImageView>(R.id.hulk)
        imgHulk.setOnClickListener {
            imgHulk.animate().apply {
                duration = 2000
                rotationYBy(360f)
            }.withEndAction {
                imgHulk.animate().apply {
                    duration = 10
                    rotationY(180f)
                }.start()
            }
        }

        // Keep the Rate Us button visible, but inactive.
        val rateUsButton = findViewById<Button>(R.id.rate_us)
        rateUsButton.setOnClickListener {
            // Intentionally left blank.
        }
    }

    fun startGame(v: View) {
        val intent = Intent(this@MainActivity, GameActivity::class.java)
        intent.putExtra("time", timeCopy)
        startActivity(intent)
    }

    fun gameSettings(v: View) {
        val intent = Intent(this, GameSetting::class.java)
        startActivity(intent)
    }
}