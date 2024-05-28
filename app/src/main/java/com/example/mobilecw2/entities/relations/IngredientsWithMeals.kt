package com.example.mobilecw2.entities.relations

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation
import com.example.mobilecw2.entities.Ingredient
import com.example.mobilecw2.entities.Meal

//Meal and ingredient table many to many relationship
@Entity
data class IngredientsWithMeals(
    @Embedded val ingredients : Ingredient,
    @Relation(
        parentColumn = "ingredientName",
        entityColumn = "mealName",
        associateBy = Junction(MealIngredientCrossReference::class)
    )
    val meals: List<Meal>
)
