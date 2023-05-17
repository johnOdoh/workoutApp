package com.example.workoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import com.example.workoutapp.databinding.ActivityExerciseBinding

class ExerciseActivity : AppCompatActivity() {
    private var binding: ActivityExerciseBinding? = null
    private var timer: CountDownTimer? = null
    private var initialRestTime = 10 //initial rest interval in seconds
    private var timeElapsed = 0 //initial value of the exercise timer in seconds
    private var timeRemaining = 0L //needed for when the exercise timer is paused in miliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //prepare the action bar
        setSupportActionBar(binding?.exerciseToolbar)
        if (supportActionBar != null) supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.exerciseToolbar?.setNavigationOnClickListener {
            onBackPressed()
        }

        //when the next exercise button is clicked
        binding?.nextBtn?.setOnClickListener{
            timer?.cancel()
            prepareRestLayout()
            startRestTimer()
        }

        //when the exercise timer is paused or played
        binding?.pauseBtn?.setOnClickListener{
            if (it.tag == "pause"){
                timer?.cancel()
                it.tag = "play"
                binding?.pauseImg?.setImageResource(R.drawable.play_button)
            }else{
                startExercise(timeRemaining)
                it.tag = "pause"
                binding?.pauseImg?.setImageResource(R.drawable.pause)
            }
        }
        startRestTimer()
    }

    //start rest interval
    private fun startRestTimer(){
        binding?.restProgressBar?.max = initialRestTime
        binding?.restProgressBar?.progress = initialRestTime
        binding?.progressTimer?.text = initialRestTime.toString()
        timer = object : CountDownTimer(initialRestTime * 1000L, 1000){
            override fun onTick(millisUntilFinished: Long) {
                initialRestTime -= 1
                binding?.restProgressBar?.progress = initialRestTime
                binding?.progressTimer?.text = (initialRestTime).toString()
            }
            override fun onFinish() {
                prepareExerciseLayout()
                startExercise(3600000)
                Toast.makeText(this@ExerciseActivity, "Start Exercise!!!", Toast.LENGTH_LONG).show()
            }
        }.start()
    }

    //start the next exercise
    private fun startExercise(duration: Long){
        timer = object : CountDownTimer(duration, 1000){
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                timeElapsed++
                val hours = (timeElapsed / 3600)
                val minutes = ((timeElapsed / 60) % 60)
                val seconds = ((timeElapsed / 1) % 60)
                if (hours > 0) binding?.progressTimer?.text = "$hours:$minutes:$seconds"
                else binding?.progressTimer?.text = "$minutes:$seconds"
            }
            override fun onFinish() {
                binding?.progressTimer?.text = "FINISHED"
                binding?.pauseBtn?.visibility = View.GONE
            }
        }.start()
    }

    //show the exercise screen
    private fun prepareExerciseLayout(){
        binding?.actionText?.text = "Start Exercise...\n Determination is the key!"
        binding?.llTextView?.layoutParams?.height = (resources.displayMetrics.density * 100).toInt()
        binding?.llTextView?.layoutParams?.width = (resources.displayMetrics.density * 100).toInt()
        binding?.restProgressBar?.visibility = View.GONE
        binding?.nextBtn?.visibility = View.VISIBLE
        binding?.pauseBtn?.visibility = View.VISIBLE
    }

    //show the rest screen
    private fun prepareRestLayout(){
        binding?.actionText?.text = "get ready to start..."
        binding?.llTextView?.layoutParams?.height = (resources.displayMetrics.density * 60).toInt()
        binding?.llTextView?.layoutParams?.width = (resources.displayMetrics.density * 60).toInt()
        binding?.pauseBtn?.visibility = View.GONE
        binding?.nextBtn?.visibility = View.GONE
        binding?.restProgressBar?.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        timer?.cancel()
    }
}