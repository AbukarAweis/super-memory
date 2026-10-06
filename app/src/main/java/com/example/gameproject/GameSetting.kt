package com.example.gameproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.ComponentActivity

class GameSetting : ComponentActivity() {

    lateinit var sharedPreferences: SharedPreferences

    var time: Long = 40000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_setting)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        time = sharedPreferences.getLong("time", 0)

        // Difficulty checkboxes: Easy, Normal, and Hard.
        val imgCheckBox: Array<ImageButton> =
            arrayOf(
                findViewById(R.id.easyCheckBox),
                findViewById(R.id.normalCheckBox),
                findViewById(R.id.hardCheckBox)
            )

        // Normal is checked by default.
        for (i in 0..2) {
            if (i != 1) {
                imgCheckBox[i].setImageResource(R.drawable.uncheck)
            } else {
                imgCheckBox[i].setImageResource(R.drawable.check)
            }
        }

        var lastChecked = imgCheckBox[1]

        // Load the previously selected difficulty.
        var selectedDifficultyIndex =
            sharedPreferences.getInt("selected_difficulty", 1)

        for (x in 0..2) {
            if (x == selectedDifficultyIndex) {
                imgCheckBox[x].setImageResource(R.drawable.check)
                lastChecked = imgCheckBox[x]
            } else {
                imgCheckBox[x].setImageResource(R.drawable.uncheck)
            }

            imgCheckBox[x].setOnClickListener {
                if (imgCheckBox[x] == lastChecked) {
                    return@setOnClickListener
                }

                imgCheckBox[x].setImageResource(R.drawable.check)
                lastChecked.setImageResource(R.drawable.uncheck)
                lastChecked = imgCheckBox[x]

                time = when (x) {
                    0 -> 60000
                    2 -> 20000
                    else -> 40000
                }

                selectedDifficultyIndex = x

                // Save difficulty and timer setting.
                val editor = sharedPreferences.edit()
                editor.putInt("selected_difficulty", x)
                editor.putLong("time", time)
                editor.apply()
            }
        }

        // Return to the home screen.
        val backToHome: ImageView = findViewById(R.id.backToHome)
        backToHome.setOnClickListener {
            val intent = Intent(this@GameSetting, MainActivity::class.java)

            if (time == 0L) {
                time = 40000
            }

            intent.putExtra("time", time)
            startActivity(intent)
        }
    }
}