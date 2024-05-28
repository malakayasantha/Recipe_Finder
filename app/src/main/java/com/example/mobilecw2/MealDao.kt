package com.example.mobilecw2

import androidx.room.*
import com.example.mobilecw2.entities.Ingredient
import com.example.mobilecw2.entities.Meal
import com.example.mobilecw2.entities.relations.IngredientsWithMeals
import com.example.mobilecw2.entities.relations.MealIngredientCrossReference

@Dao
interface MealDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: Meal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: Ingredient)

    @Query("Select * from meal")
    suspend fun getMeal() : List<Meal>

    @Query("Select * from ingredient Where ingredientName Like '%' || :query || '%'")
    suspend fun getIngredientsWithMeals(query: String) : List<IngredientsWithMeals>

    @Query("Select * from meal Where mealName Like '%' || :query || '%'")
    suspend fun searchMeal(query: String) : List<Meal>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRef(ingredientCrossRef : MealIngredientCrossReference)
}