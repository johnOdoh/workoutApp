package com.example.workoutapp

data class ExerciseModel(
    val id: Int,
    val name: String,
    val image: Int,
    val description: String,
    val hasDuration: Boolean,
    val duration: Int? = null,
    val number: Int? = null
)
