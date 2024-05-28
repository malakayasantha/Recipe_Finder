package com.example.mobilecw2.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

//Meal table
@Entity
data class Meal(
    @PrimaryKey(autoGenerate = false) val mealName: String,

    val mealCategory: String,
    val mealArea: String,
    val mealInstructions: String,
    val mealThumbnail: String,
    val mealTags: String?,
    val mealYoutube: String,
    val mealIngredients: String,
)
