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
        val bonusT = findViewById<TextView>(R.id.bonus)
        val count = findViewById<TextView>(R.id.count)
        val quit = findViewById<Button>(R.id.quit)

        //Storing two copies of each of the super hero images
        val images: MutableList<Int> = mutableListOf(_captain_america, _deadpool, _groot, _hulk, _iron_man, _spiderman,
            _captain_america, _deadpool, _groot, _hulk, _iron_man, _spiderman)

        //Storing each of the image buttons
        val buttons: Array<ImageButton> =
            arrayOf(findViewById(R.id.imageButton1),
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
                findViewById(R.id.imageButton12))

        numMatchesRemain = images.size.div(2)
        count.text = "Matches: ${numMatchesRemain} / ${numMatchesRemain}"

        //Shuffle the cards so the position of the cards is different each time onCreate() is executed
        images.shuffle()

        for (i:Int in 0..11) {
            //Set tag of all buttons in the array to cardBack, which indicates the card is face down
            buttons[i].tag = "cardBack"
            buttons[i].setImageResource(cardBack)

            /*
            When a card is clicked, check the tag to see if the card is face down (cardBack) or not.
            If true -> change image of the card to one of the super hero images
            If false -> change image of the card to the default image (question mark)
             */
            buttons[i].setOnClickListener{
                if (buttons[i].tag == "cardBack" && canTurnOver) {
                    buttons[i].setImageResource(images[i])
                    buttons[i].tag = images[i]
                    if (numCardsFaceUp == 0) {
                        lastClicked = i     //keep track of the most recent card turned over
                    }
                    numCardsFaceUp++
                } else if (buttons[i].tag != "cardBack") {
                    buttons[i].setImageResource(cardBack)
                    buttons[i].tag = "cardBack"
                    numCardsFaceUp--
                }

                /*
                When there are two cards facing up, don't allow any other cards to be turned over and
                do one of the following depending on the two face up cards:

                -The two cards match -> keep both cards face up and don't let the user click them anymore
                -The two cards don't match -> wait for half a second then change the image and the tag
                    of both cards to the default cardBack. This will appear to flip the cards over in
                    the app.

                 After either of these code blocks execute, make sure allow the user to turn over every
                 face down card and reset the number of face up cards to zero.
                 */
                if (numCardsFaceUp == 2) {
                    canTurnOver = false

                    if (buttons[i].tag == buttons[lastClicked].tag) {
                        buttons[i].isClickable = false
                        buttons[lastClicked].isClickable = false

                        /*
                        If there are no more matches, the game is over.
                        Play the win animation
                         */
                        match = true
                        numMatchesRemain--
                        count.text = "Matches: $numMatchesRemain/6"

                        if (numMatchesRemain == 0) {
                            gameWon = true
                            winAnimation()
                        }
                        /*
                        When there is a match, change the color of the timer to green, then
                        wait for 250ms and change the color of the timer back to purple

                        Adjust the visibility of the bonus textView so it flashes on the screen when
                        there is a match
                         */
                        if (!gameWon) {
                            timer.setTextColor(Color.parseColor("#4CAF50"))
                            bonusT.text = "+${bonus / 1000}"
                            bonusT.visibility = VISIBLE
                        }

                        handler.postDelayed( Runnable {
                            run {
                                timer.setTextColor(Color.parseColor("#9B27AF"))
                                bonusT.visibility = INVISIBLE
                            }
                        }, 500)
                    } else {
                        handler.postDelayed( Runnable {
                            run {
                                buttons[i].tag = "cardBack"
                                buttons[i].setImageResource(cardBack)
                                buttons[lastClicked].tag = "cardBack"
                                buttons[lastClicked].setImageResource(cardBack)
                                //match = false
                            }
                        }, 500)
                    }
                    canTurnOver = true
                    numCardsFaceUp = 0
                } else if (numCardsFaceUp == 0) {
                    canTurnOver = true
                }
            }
        }

        quit.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            // Start the settings activity
            startActivity(intent)
        }

        //user recreate method to reset everything when the button is pressed
        reset.setOnClickListener{
            gameReset = true
            recreate()
        }
    }

    /*
    A timer that will count down and constantly update the timer textView with a new value
    every second.
     */
    private fun createCountDownTimer() {
        val timer = findViewById<TextView>(R.id.timer)

        /*
        A new countdown timer is created when onCreate is run. It stores it current count
        every second and, if a match is made, it increases the current count by 5 seconds
        and updates the textView. Then it cancels the current countdown timer and creates
        a new one with whatever number the previous countdown was at before it was terminated
         */
        object : CountDownTimer(countdownPeriod, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timer.text = "Time: " + millisUntilFinished / 1000
                countdownPeriod = millisUntilFinished

                if (match) {
                    countdownPeriod = (millisUntilFinished + bonus)
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

            /*
            When the time reaches zero or the game is won,
            update the textView accordingly
             */
            override fun onFinish() {
                if (gameWon) { //all matches found
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
        val buttons: Array<ImageButton> =
            arrayOf(findViewById(R.id.imageButton1),
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
                findViewById(R.id.imageButton12))

        /*
        If the game was won -> take each imageButton, apply the animation using
        rotate(), then click the button which will play the animation

        If the game was lost -> change any face down cards (the ones with question marks)
        to have an black quesiton mark icon, and don't allow any of the imageButtons to be pressed
        -repeat this twice in case a card was flipped at the very last second

         */
        for (i:Int in 0..11) {
            var img = buttons[i]
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
            }.withEndAction() {
                img.animate().apply {
                    duration = 10
                    //rotationY(180f)
                }.start()
            }
        }
    }

}