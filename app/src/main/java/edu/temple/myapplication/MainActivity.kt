package edu.temple.myapplication

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var countdownText: TextView
    private lateinit var startButton: Button
    private lateinit var pauseButton: Button

    private var countdownTimer: CountDownTimer? = null
    private var isPaused = false
    private var timeLeftInMillis: Long = DEFAULT_TIME

    companion object {
        const val DEFAULT_TIME: Long = 100_000 // 100 seconds in milliseconds
        const val PREFS_NAME = "CountdownPrefs"
        const val TIME_LEFT_KEY = "time_left"
        const val IS_PAUSED_KEY = "is_paused"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        countdownText = findViewById(R.id.countdownText)
        startButton = findViewById(R.id.startButton)
        pauseButton = findViewById(R.id.stopButton)

        // Load saved state
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isPaused = prefs.getBoolean(IS_PAUSED_KEY, false)
        if (isPaused) {
            timeLeftInMillis = prefs.getLong(TIME_LEFT_KEY, DEFAULT_TIME)
        } else {
            timeLeftInMillis = DEFAULT_TIME
        }
        updateCountdownText()

        startButton.setOnClickListener {
            startCountdown()
        }

        pauseButton.setOnClickListener {
            pauseCountdown()
        }
    }

    private fun startCountdown() {
        countdownTimer?.cancel() // Cancel any existing timer

        countdownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountdownText()
            }

            override fun onFinish() {
                timeLeftInMillis = DEFAULT_TIME
                isPaused = false
                updateCountdownText()
                clearSavedTime()
            }
        }.start()

        isPaused = false
        savePausedState()
    }

    private fun pauseCountdown() {
        countdownTimer?.cancel()
        isPaused = true
        savePausedTime(timeLeftInMillis)
    }

    private fun updateCountdownText() {
        val secondsLeft = (timeLeftInMillis / 1000).toInt()
        countdownText.text = "Time Left: $secondsLeft seconds"
    }

    private fun savePausedTime(time: Long) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putLong(TIME_LEFT_KEY, time)
            putBoolean(IS_PAUSED_KEY, true)
            apply()
        }
    }

    private fun savePausedState() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putBoolean(IS_PAUSED_KEY, false)
            remove(TIME_LEFT_KEY)
            apply()
        }
    }


    private fun clearSavedTime() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            clear()
            apply()
        }
    }
}
