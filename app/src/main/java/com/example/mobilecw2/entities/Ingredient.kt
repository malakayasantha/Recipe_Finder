package com.example.mobilecw2.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

//Ingredient table
@Entity
data class Ingredient(
    @PrimaryKey(autoGenerate = false) val ingredientName : String,
)
