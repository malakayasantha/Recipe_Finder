package com.example.mobilecw2

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mobilecw2.entities.Ingredient
import com.example.mobilecw2.entities.Meal
import com.example.mobilecw2.entities.relations.MealIngredientCrossReference


@Database(
    entities = [
        Meal::class,
        Ingredient::class,
        MealIngredientCrossReference::class
    ],
    version = 2,
)
abstract class MealsDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
}
