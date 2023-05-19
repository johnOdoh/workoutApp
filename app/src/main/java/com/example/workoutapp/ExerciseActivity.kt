package com.example.workoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.workoutapp.databinding.ActivityExerciseBinding

class ExerciseActivity : AppCompatActivity() {
    private var binding: ActivityExerciseBinding? = null
    private var timer: CountDownTimer? = null
    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var initialRestTime = 10 //initial rest interval in seconds
    private var timeElapsed = 0 //initial value of the exercise timer in seconds
    private var timeRemaining = 0L //needed for when the exercise timer is paused in milliseconds
    private var currentExerciseIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //load the exercises
        exerciseList = Constants.getExercises()

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
        }

        //when the exercise timer is paused or played
        binding?.pauseBtn?.setOnClickListener{
            if (it.tag == "pause"){
                timer?.cancel()
                it.tag = "play"
                binding?.pauseImg?.setImageResource(R.drawable.play_button)
            }else{
                startExercise(timeRemaining, exerciseList!![currentExerciseIndex].hasDuration)
                it.tag = "pause"
                binding?.pauseImg?.setImageResource(R.drawable.pause)
            }
        }
        startRestTimer() //start the countdown to exercise on screen load
    }

    //show the rest screen
    private fun prepareRestLayout(){
        binding?.restScreen?.visibility = View.VISIBLE
        binding?.exerciseScreen?.visibility = View.GONE
        if (!exerciseList!![currentExerciseIndex].hasDuration && exerciseList!![currentExerciseIndex + 1].hasDuration) {
            binding?.exerciseProgressBar?.visibility = View.VISIBLE
            binding?.llExerciseTimer?.layoutParams?.width = (resources.displayMetrics.density * 60).toInt()
            binding?.llExerciseTimer?.layoutParams?.height = (resources.displayMetrics.density * 60).toInt()
        }
        startRestTimer()
    }

    //start rest interval
    private fun startRestTimer(){
        binding?.restProgressBar?.max = initialRestTime
        binding?.restProgressBar?.progress = initialRestTime
        binding?.restTimer?.text = initialRestTime.toString()
        timer = object : CountDownTimer(initialRestTime * 1000L, 1000){
            override fun onTick(millisUntilFinished: Long) {
                initialRestTime--
                binding?.restProgressBar?.progress = initialRestTime
                binding?.restTimer?.text = (initialRestTime).toString()
            }
            override fun onFinish() {
                prepareExerciseLayout()
            }
        }.start()
    }

    //show the exercise screen
    private fun prepareExerciseLayout(){
        currentExerciseIndex++
        if (currentExerciseIndex < 1) binding?.previousBtn?.visibility = View.GONE
        else binding?.previousBtn?.visibility = View.VISIBLE
        if (currentExerciseIndex >= exerciseList!!.size - 1) binding?.nextBtn?.visibility = View.GONE
        val currentExercise = exerciseList!![currentExerciseIndex]
        initialRestTime = 10
        timeElapsed = 0
        binding?.restScreen?.visibility = View.GONE
        binding?.exerciseScreen?.visibility = View.VISIBLE
        binding?.exerciseActionText?.text = currentExercise.name
        binding?.exerciseImg?.setImageResource(currentExercise.image)
        if (currentExercise.hasDuration && currentExercise.duration != null){ //if the exercise has a duration
            timeElapsed = currentExercise.duration
            startExercise(currentExercise.duration * 1000L, true)
        }else if (!currentExercise.hasDuration && currentExercise.number != null){ //if it doesn't have a duration
            binding?.exerciseProgressBar?.visibility = View.GONE
            binding?.llExerciseTimer?.layoutParams?.width = (resources.displayMetrics.density * 100).toInt()
            binding?.llExerciseTimer?.layoutParams?.height = (resources.displayMetrics.density * 100).toInt()
            startExercise(3600000, false)
        }else { //if there was a mix up in data entry
            timeElapsed = 30
            startExercise(30000, true)
        }
    }

    //start the next exercise
    private fun startExercise(duration: Long, hasDuration: Boolean){
        binding?.exerciseProgressBar?.max = timeElapsed
        binding?.exerciseProgressBar?.progress = timeElapsed
        timer = object : CountDownTimer(duration, 1000){
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                if (!hasDuration){
                    timeElapsed++
                    val hours = (timeElapsed / 3600)
                    val minutes = ((timeElapsed / 60) % 60)
                    val seconds = ((timeElapsed / 1) % 60)
                    if (hours > 0) binding?.exerciseTimer?.text = "$hours:$minutes:$seconds"
                    else binding?.exerciseTimer?.text = "$minutes:$seconds"
                }else {
                    timeElapsed--
                    binding?.exerciseProgressBar?.progress = timeElapsed
                    binding?.exerciseTimer?.text = timeElapsed.toString()
                }
            }
            override fun onFinish() {
                if (currentExerciseIndex >= exerciseList!!.size - 1) binding?.exerciseTimer?.text = "FINISHED"
                else prepareRestLayout()

            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        if(timer != null) timer?.cancel()
    }
}