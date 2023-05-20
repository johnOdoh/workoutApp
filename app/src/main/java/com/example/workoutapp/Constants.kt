package com.example.workoutapp

object Constants {
    fun getExercises(): ArrayList<ExerciseModel> {
        return arrayListOf(
            ExerciseModel(
                3,
                "Jumping Jacks",
                R.drawable.ic_jumping_jacks,
                "This exercise helps build your upper body muscles..." +
                        "Inhale while raising your hands and exhale while bringing them down",
                true,
                30
            ), ExerciseModel(
                1,
                "Abdominal Crunches",
                R.drawable.ic_abdominal_crunch,
                "This exercise helps build your abdominal muscles... " +
                        "Inhale while raising your body and exhale while bringing it down",
                false,
                null,
                15
            ), ExerciseModel(
                2,
                "High knees running in place",
                R.drawable.ic_high_knees_running_in_place,
                "This exercise helps strengthen your knees and thigh muscles",
                true,
                30,
                null
            ), ExerciseModel(
                4,
                "Lunges",
                R.drawable.ic_lunge,
                "This exercise helps strengthen your knees and thighs",
                false,
                null,
                10
            ), ExerciseModel(
                6,
                "Push Ups",
                R.drawable.ic_push_up,
                "This exercise builds your chest and bicep muscles..." +
                        "Ensure to keep your body straight... Inhale while raising your body and exhale while bringing it down",
                false,
                null,
                10
            ), ExerciseModel(
                7,
                "Push ups and rotation",
                R.drawable.ic_push_up_and_rotation,
                "This exercise helps build your upper body muscles",
                false,
                null,
                10
            ), ExerciseModel(
                5,
                "Plank",
                R.drawable.ic_plank,
                "This exercise helps build your abs and increases your endurance..." +
                        "Make sure your body is straight. Do not raise or sink your waist",
                true,
                30
            ), ExerciseModel(
                8,
                "Side Plank",
                R.drawable.ic_side_plank,
                "This exercise helps build your endurance",
                true,
                30
            )
        )
    }
}