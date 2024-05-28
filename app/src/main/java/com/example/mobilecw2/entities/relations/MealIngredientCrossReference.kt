package com.example.mobilecw2.entities.relations

import androidx.room.Entity

@Entity(primaryKeys = ["mealName", "ingredientName"])
data class MealIngredientCrossReference(
    val mealName: String,
    val ingredientName: String,
)
