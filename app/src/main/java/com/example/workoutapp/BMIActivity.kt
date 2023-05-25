package com.example.workoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.workoutapp.databinding.ActivityBmiBinding
import kotlin.math.pow

class BMIActivity : AppCompatActivity() {
    private var binding: ActivityBmiBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.bmiToolbar)
        binding?.bmiToolbar?.title = "BMI Calculator"
        if (supportActionBar != null) supportActionBar?.setDisplayHomeAsUpEnabled(true) //show back arrow for the action bar
        binding?.bmiToolbar?.setNavigationOnClickListener { //what will happen when the back arrow is pressed
            onBackPressed()
        }

//        binding?.btnSubmit?.isEnabled = false
        binding?.btnSubmit?.setOnClickListener{
            val weight = binding?.etWeight?.text.toString().toFloatOrNull()
            val height = binding?.etHeight?.text.toString().toFloatOrNull()
            val weightUnit = binding?.weightSpinner?.selectedItem.toString()
            val heightUnit = binding?.heightSpinner?.selectedItem.toString()
            when{
                weight == null && height == null -> {
                    binding?.tiWeight?.error = "Field Required"
                    binding?.tiHeight?.error = "Field Required"
                }
                weight == null -> {
                    binding?.tiHeight?.error = null
                    binding?.tiWeight?.error = "Field Required"
                }
                height == null -> {
                    binding?.tiWeight?.error = null
                    binding?.tiHeight?.error = "Field Required"
                }
                else -> {
                    binding?.tiWeight?.error = null
                    binding?.tiHeight?.error = null
                    displayBMIResult(calculateBMI(weight, height, weightUnit, heightUnit))
                }
            }
//            Log.i("units", "$weight, $height")
        }
    }

    private fun calculateBMI(bodyWeight: Float, bodyHeight: Float, weightUnit: String, heightUnit: String): Float{
        var weight = bodyWeight
        var height = bodyHeight
        if (heightUnit == "Feet") height *= 0.3048f
        if (weightUnit == "Pounds") weight *= 0.45359237f
        return (weight / height.pow(2))
    }

    private fun displayBMIResult(bmi: Float){
        var category = ""
        var advice = ""
        when(bmi){
           in 0f..18.4f -> {
                category = "Underweight"
                advice = "Improve your diet. Eat more fatty foods to build you weight"
            }
            in 18.5f..24.9f -> {
                category = "Normal"
                advice = "Congrats on working on your weight, keep it up."
            }
            in 25f..29.9f -> {
                category = "Overweight"
                advice = "You should eat more of fruits and vegetables. You also need to exercise more"
            }
            in 30f..39f -> {
                category = "Obese"
                advice = "You need to be on very strict diet. Also, you need to do lots of exercise. Please consult a nutritionist"
            }
            in 40f..80f -> {
                category = "Severely Obese"
                advice = "Please consult your doctor immediately"
            }
            else -> {
                category = "Inconclusive"
                advice = "Please make sure you have entered your correct weight and height, and chosen the correct units, then try again"
            }
        }
        binding?.bmiResultText?.text = "Your BMI is ${String.format("%.2f", bmi)}. You are $category"
        binding?.adviceText?.text = "$advice"
        binding?.llResult?.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}