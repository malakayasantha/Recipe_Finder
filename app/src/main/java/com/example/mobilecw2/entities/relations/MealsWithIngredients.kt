package com.example.mobilecw2.entities.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.mobilecw2.entities.Ingredient
import com.example.mobilecw2.entities.Meal

//Meal and ingredient table many to many relationship
data class MealsWithIngredients(
    @Embedded
    val meals: Meal,
    @Relation(
        parentColumn = "mealName",
        entityColumn = "ingredientName",
        associateBy = Junction(MealIngredientCrossReference::class)
    )
    val ingredients : List<Ingredient>
)
