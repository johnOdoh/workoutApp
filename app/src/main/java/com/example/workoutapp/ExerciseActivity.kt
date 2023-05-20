package com.example.workoutapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.workoutapp.databinding.ActivityExerciseBinding
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding: ActivityExerciseBinding? = null
    private var timer: CountDownTimer? = null
    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var text2Speech: TextToSpeech? = null
    private var initialRestTime = 10 //initial rest interval in seconds
    private var timeElapsed = 0 //initial value of the exercise timer in seconds
    private var timeRemaining = 0L //needed for when the exercise timer is paused; in milliseconds
    private var currentExerciseIndex = -1 //current index of the exercise arrayList
    private var currentExercise: ExerciseModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        exerciseList = Constants.getExercises() //load the exercises

        text2Speech = TextToSpeech(this, this) //initialize to text2speech object

        //prepare the action bar
//        setSupportActionBar(binding?.exerciseToolbar)
//        if (supportActionBar != null) supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        binding?.exerciseToolbar?.setNavigationOnClickListener {
//            onBackPressed()
//        }

        //when the 'next' button is clicked
        binding?.nextBtn?.setOnClickListener{
            if (currentExerciseIndex >= exerciseList!!.size - 1) { //if it is the last exercise
                timer?.cancel()
                val intent = Intent(this@ExerciseActivity, ExerciseCompletedActivity::class.java)
                startActivity(intent)
            }else {
                timer?.cancel()
                prepareRestLayout()
            }
        }

        //when the 'previous' button is clicked
        binding?.previousBtn?.setOnClickListener{
            timer?.cancel()
            currentExerciseIndex -= 2
            prepareRestLayout()
        }

        //when the exercise timer is paused or played
        binding?.pauseBtn?.setOnClickListener{
            if (it.tag == "pause"){
                timer?.cancel()
                it.tag = "play"
                binding?.pauseImg?.setImageResource(R.drawable.play_button)
            }else{
                it.tag = "pause"
                binding?.pauseImg?.setImageResource(R.drawable.pause)
                startExercise(timeRemaining, exerciseList!![currentExerciseIndex].hasDuration)
            }
        }
        prepareRestLayout() //start the countdown to exercise on screen load
    }

    //show the rest screen
    @SuppressLint("SetTextI18n")
    private fun prepareRestLayout(){
        currentExerciseIndex++
        currentExercise = exerciseList!![currentExerciseIndex]
        if (exerciseList!![currentExerciseIndex].hasDuration){
            binding?.nextExerciseText?.text = "${currentExercise!!.duration} seconds\n" + currentExercise!!.name
        } else {
            binding?.nextExerciseText?.text = "${currentExercise!!.number}\n" + currentExercise!!.name
        }
        binding?.restScreen?.visibility = View.VISIBLE
        binding?.exerciseScreen?.visibility = View.GONE

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
                val sentence = if (!currentExercise!!.hasDuration) "Get ready for... ${currentExercise!!.number}, ${currentExercise!!.name}"
                else "Get ready for... ${currentExercise!!.duration} seconds, ${currentExercise!!.name}"
                if (initialRestTime == 8) speakOut(sentence)
                binding?.restProgressBar?.progress = initialRestTime
                binding?.restTimer?.text = (initialRestTime).toString()
            }
            override fun onFinish() {
                prepareExerciseLayout()
            }
        }.start()
    }

    //show the exercise screen
    @SuppressLint("SetTextI18n")
    private fun prepareExerciseLayout(){
        //prepare the text2speech for this exercise
        val sentence = if (!currentExercise!!.hasDuration) "${currentExercise!!.number}, ${currentExercise!!.name}... ${currentExercise!!.description}"
        else "${currentExercise!!.duration} seconds, ${currentExercise!!.name}... ${currentExercise!!.description}"
        speakOut(sentence)
        //if this is the first exercise, hide the 'previous' button
        if (currentExerciseIndex < 1) binding?.previousBtn?.visibility = View.GONE
        else binding?.previousBtn?.visibility = View.VISIBLE
        initialRestTime = 10
        timeElapsed = 0
        binding?.restScreen?.visibility = View.GONE
        binding?.exerciseScreen?.visibility = View.VISIBLE
        binding?.exerciseImg?.setImageResource(currentExercise!!.image)
        if (currentExercise!!.hasDuration && currentExercise!!.duration != null){ //if the exercise has a duration
            binding?.exerciseActionText?.text = "${currentExercise!!.duration} seconds\n${currentExercise!!.name}"
            binding?.nextBtn?.visibility = View.GONE //hide the 'next' button
            binding?.exerciseProgressBar?.visibility = View.VISIBLE //show the progressbar
            //change the layout params to 60dp
            binding?.llExerciseTimer?.layoutParams?.width = (resources.displayMetrics.density * 60).toInt()
            binding?.llExerciseTimer?.layoutParams?.height = (resources.displayMetrics.density * 60).toInt()
            startExercise(currentExercise!!.duration!! * 1000L, true)
        }else if (!currentExercise!!.hasDuration && currentExercise!!.number != null){ //if it doesn't have a duration
            binding?.exerciseActionText?.text = "${currentExercise!!.number}\n${currentExercise!!.name}"
            binding?.nextBtn?.visibility = View.VISIBLE //show the 'next' button
            binding?.exerciseProgressBar?.visibility = View.GONE //hide the progressbar
            //change the layout params to 100dp
            binding?.llExerciseTimer?.layoutParams?.width = (resources.displayMetrics.density * 100).toInt()
            binding?.llExerciseTimer?.layoutParams?.height = (resources.displayMetrics.density * 100).toInt()
            startExercise(3600000, false)
        }else { //if there was a mix up in data entry
            binding?.nextBtn?.visibility = View.GONE
            startExercise(30000, true)
        }
    }

    //start the next exercise
    private fun startExercise(duration: Long, hasDuration: Boolean){
        binding?.exerciseProgressBar?.max = (duration/1000).toInt()
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
                    binding?.exerciseProgressBar?.progress = (timeRemaining / 1000).toInt()
                    binding?.exerciseTimer?.text = (timeRemaining / 1000).toString()
                }
            }
            override fun onFinish() {
                if (currentExerciseIndex >= exerciseList!!.size - 1) { //if it is the last exercise
                    val intent = Intent(this@ExerciseActivity, ExerciseCompletedActivity::class.java)
                    startActivity(intent)
                }
                else prepareRestLayout()
            }
        }.start()
    }

    private fun speakOut(sentence: String){
        text2Speech?.speak(sentence, TextToSpeech.QUEUE_FLUSH, null,null)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        if(timer != null) timer?.cancel()
        if (text2Speech != null){
            text2Speech?.stop()
            text2Speech?.shutdown()
        }
    }

    override fun onPause() {
        super.onPause()
        //behave as though the exercise was paused
        if(timer != null) timer?.cancel()
        binding?.pauseBtn?.tag = "play"
        binding?.pauseImg?.setImageResource(R.drawable.play_button)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS){
            Toast.makeText(this, "Text to speech enabled", Toast.LENGTH_SHORT).show()
            text2Speech?.setSpeechRate(0.8f)
            val result = text2Speech?.setLanguage(Locale.getDefault())
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(this, "Text To Speech language not available", Toast.LENGTH_LONG).show()
            }
        } else Toast.makeText(this, "Text To Speech currently not available", Toast.LENGTH_LONG).show()
    }
}