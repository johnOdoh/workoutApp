package com.example.workoutapp

object Constants {
    fun getExercises(): ArrayList<ExerciseModel> {
        return arrayListOf(
            ExerciseModel(
                1,
                "Abdominal Crunch",
                R.drawable.ic_abdominal_crunch,
                "This exercise helps build your upper body muscles",
                false,
                null,
                15
            ), ExerciseModel(
                2,
                "High knees running in place",
                R.drawable.ic_high_knees_running_in_place,
                "This exercise helps build your upper body muscles",
                false,
                null,
                15
            ), ExerciseModel(
                3,
                "Jumping Jacks",
                R.drawable.ic_jumping_jacks,
                "This exercise helps build your upper body muscles",
                true,
                30
            ), ExerciseModel(
                4,
                "Lunge",
                R.drawable.ic_lunge,
                "This exercise helps build your upper body muscles",
                false,
                null,
                10
            ), ExerciseModel(
                5,
                "Plank",
                R.drawable.ic_plank,
                "This exercise helps build your upper body muscles",
                true,
                30
            ), ExerciseModel(
                6,
                "Push Up",
                R.drawable.ic_push_up,
                "This exercise helps build your upper body muscles",
                false,
                null,
                10
            ), ExerciseModel(
                7,
                "Push up and rotation",
                R.drawable.ic_push_up_and_rotation,
                "This exercise helps build your upper body muscles",
                false,
                null,
                10
            ), ExerciseModel(
                8,
                "Side Plank",
                R.drawable.ic_side_plank,
                "This exercise helps build your upper body muscles",
                true,
                30
            )
        )
    }
}