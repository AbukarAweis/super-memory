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

        // =======================================================================
        //  Creates an array of ImageButtons
        // =======================================================================
        val img_checkBox: Array<ImageButton> =
            arrayOf(findViewById(R.id.easyCheckBox),
                findViewById(R.id.normalCheckBox),
                findViewById(R.id.hardCheckBox),
                findViewById(R.id.demoCheckBox))

        var numChecked = 0
        // ==================================================================================
        //  Loop that presets the ImageButtons to either a checked image or unchecked
        // ==================================================================================

        for(i in 0..3)  //ToDO: Demo purpose 0 .. 3, change range back to 0 .. 2 when demo is over
        {
            if(i != 1)  //Sets all ImageButtons as unchecked, except the default ImageButton
            {
                img_checkBox[i].setImageResource(R.drawable.uncheck)
            }

            else    //This ImageButton will be checked by default at the beginning of load up
            {
                img_checkBox[i].setImageResource(R.drawable.check)
            }

        }

        var lastChecked = img_checkBox[1]   //ImageButton that was last checked

        // ==================================================================================
        //  Allows user to select a difficulty and sets a certain amount of time based on
        //  the chosen difficulty
        // ==================================================================================
        var selectedDifficultyIndex = sharedPreferences.getInt("selected_difficulty", 1)

        for (x in 0..3) {   //ToDO: Demo purpose 0 .. 3, change range back to 0 .. 2 when demo is over
            if (x == selectedDifficultyIndex) {
                img_checkBox[x].setImageResource(R.drawable.check)
                lastChecked = img_checkBox[x]
            } else {
                img_checkBox[x].setImageResource(R.drawable.uncheck)
            }

            img_checkBox[x].setOnClickListener {
                if (img_checkBox[x] == lastChecked) {
                    return@setOnClickListener
                }

                img_checkBox[x].setImageResource(R.drawable.check)
                lastChecked.setImageResource(R.drawable.uncheck) // Uncheck the previously checked ImageButton
                lastChecked = img_checkBox[x]

                time = when (x) {
                    0 -> 60000
                    2 -> 20000
                    3 -> 5000   //ToDo: Demo purpose, delete after demo is over
                    else -> 40000
                }

                selectedDifficultyIndex = x

                // Save the selected difficulty to SharedPreferences
                val editor = sharedPreferences.edit()
                editor.putInt("selected_difficulty", x)
                editor.putLong("time", time)
                editor.apply()
            }
        }
        // =======================================================================================
        //  When back button is clicked, return to the home screen
        // =======================================================================================
        val backToHome: ImageView = findViewById(R.id.backToHome)
        backToHome.setOnClickListener{
            val intent = Intent(this@GameSetting, MainActivity::class.java)
//            intent.putExtra("time", sharedPreferences.getLong("time", 0))

            if (time == 0L) {
                time = 40000
            }

            intent.putExtra("time", time)
            startActivity(intent)
        }
    }
}

//ToDo: NOTE: in activity_game_setting.xml delete demoSetting (TextView) and demoCheckBox(ImageButton)
// after demo is over