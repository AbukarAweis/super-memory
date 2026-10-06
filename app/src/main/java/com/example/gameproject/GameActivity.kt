package com.example.gameproject

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.gameproject.R.drawable.*

class GameActivity : ComponentActivity() {

    private var handler: Handler = Handler()

    private val cardBack = _question_mark
    private var numCardsFaceUp = 0
    private var lastClicked = -1
    private var canTurnOver = true
    var match = false

    var bonus: Long = 0
    var countdownPeriod: Long = 0

    var numMatchesRemain = 0
    var gameWon = false
    var gameLost = false
    var gameReset = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val time = intent.getLongExtra("time", 40000)

        countdownPeriod = time + 1000
        bonus = 5000

        createCountDownTimer()

        val reset = findViewById<Button>(R.id.reset)
        val timer = findViewById<TextView>(R.id.timer)
        val bonusText = findViewById<TextView>(R.id.bonus)
        val count = findViewById<TextView>(R.id.count)
        val quit = findViewById<Button>(R.id.quit)

        // Two copies of each character image.
        val images: MutableList<Int> = mutableListOf(
            _captain_america,
            _deadpool,
            _groot,
            _hulk,
            _iron_man,
            _spiderman,
            _captain_america,
            _deadpool,
            _groot,
            _hulk,
            _iron_man,
            _spiderman
        )

        // Stores each card button.
        val buttons: Array<ImageButton> = arrayOf(
            findViewById(R.id.imageButton1),
            findViewById(R.id.imageButton2),
            findViewById(R.id.imageButton3),
            findViewById(R.id.imageButton4),
            findViewById(R.id.imageButton5),
            findViewById(R.id.imageButton6),
            findViewById(R.id.imageButton7),
            findViewById(R.id.imageButton8),
            findViewById(R.id.imageButton9),
            findViewById(R.id.imageButton10),
            findViewById(R.id.imageButton11),
            findViewById(R.id.imageButton12)
        )

        numMatchesRemain = images.size.div(2)
        count.text = "Matches: ${numMatchesRemain} / ${numMatchesRemain}"

        // Randomize card positions each game.
        images.shuffle()

        for (i: Int in 0..11) {
            buttons[i].tag = "cardBack"
            buttons[i].setImageResource(cardBack)

            buttons[i].setOnClickListener {
                if (buttons[i].tag == "cardBack" && canTurnOver) {
                    buttons[i].setImageResource(images[i])
                    buttons[i].tag = images[i]

                    if (numCardsFaceUp == 0) {
                        lastClicked = i
                    }

                    numCardsFaceUp++
                } else if (buttons[i].tag != "cardBack") {
                    buttons[i].setImageResource(cardBack)
                    buttons[i].tag = "cardBack"
                    numCardsFaceUp--
                }

                // Compare the two face-up cards.
                if (numCardsFaceUp == 2) {
                    canTurnOver = false

                    if (buttons[i].tag == buttons[lastClicked].tag) {
                        buttons[i].isClickable = false
                        buttons[lastClicked].isClickable = false

                        match = true
                        numMatchesRemain--
                        count.text = "Matches: $numMatchesRemain/6"

                        if (numMatchesRemain == 0) {
                            gameWon = true
                            winAnimation()
                        }

                        // Flash the timer and display the time bonus.
                        if (!gameWon) {
                            timer.setTextColor(Color.parseColor("#4CAF50"))
                            bonusText.text = "+${bonus / 1000}"
                            bonusText.visibility = VISIBLE
                        }

                        handler.postDelayed({
                            timer.setTextColor(Color.parseColor("#9B27AF"))
                            bonusText.visibility = INVISIBLE
                        }, 500)
                    } else {
                        // Flip unmatched cards back over.
                        handler.postDelayed({
                            buttons[i].tag = "cardBack"
                            buttons[i].setImageResource(cardBack)

                            buttons[lastClicked].tag = "cardBack"
                            buttons[lastClicked].setImageResource(cardBack)
                        }, 500)
                    }

                    canTurnOver = true
                    numCardsFaceUp = 0
                } else if (numCardsFaceUp == 0) {
                    canTurnOver = true
                }
            }
        }

        quit.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Reset the current game.
        reset.setOnClickListener {
            gameReset = true
            recreate()
        }
    }

    // Countdown timer for the current game.
    private fun createCountDownTimer() {
        val timer = findViewById<TextView>(R.id.timer)

        object : CountDownTimer(countdownPeriod, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timer.text = "Time: " + millisUntilFinished / 1000
                countdownPeriod = millisUntilFinished

                // Add bonus time after a successful match.
                if (match) {
                    countdownPeriod = millisUntilFinished + bonus
                    cancel()
                    match = false
                }

                if (numMatchesRemain == 0) {
                    onFinish()
                }

                if (!gameReset) {
                    cancel()
                    createCountDownTimer()
                } else {
                    cancel()
                }
            }

            override fun onFinish() {
                if (gameWon) {
                    timer.setTextColor(Color.parseColor("#9B27AF"))
                    timer.text = "You Won!"
                    cancel()
                } else {
                    timer.text = "You Lost!"
                    gameLost = true
                    winAnimation()
                    cancel()
                }
            }
        }.start()
    }

    private fun winAnimation() {
        val buttons: Array<ImageButton> = arrayOf(
            findViewById(R.id.imageButton1),
            findViewById(R.id.imageButton2),
            findViewById(R.id.imageButton3),
            findViewById(R.id.imageButton4),
            findViewById(R.id.imageButton5),
            findViewById(R.id.imageButton6),
            findViewById(R.id.imageButton7),
            findViewById(R.id.imageButton8),
            findViewById(R.id.imageButton9),
            findViewById(R.id.imageButton10),
            findViewById(R.id.imageButton11),
            findViewById(R.id.imageButton12)
        )

        for (i: Int in 0..11) {
            val img = buttons[i]

            if (!gameLost) {
                rotateAnimation(img)
                img.callOnClick()
            } else {
                img.isClickable = false

                if (img.tag.equals("cardBack")) {
                    img.setImageResource(R.drawable._question_mark_b)
                    img.tag = "_question_mark_b"
                }
            }

            img.isClickable = false
        }
    }

    private fun rotateAnimation(img: ImageButton) {
        img.setOnClickListener {
            img.animate().apply {
                duration = 2000
                rotationYBy(360f)
            }.withEndAction {
                img.animate().apply {
                    duration = 10
                }.start()
            }
        }
    }
}