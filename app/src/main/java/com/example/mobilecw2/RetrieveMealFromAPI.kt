package com.example.mobilecw2

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class RetrieveMealFromAPI : AppCompatActivity() {

    private lateinit var mealsDao: MealDao

    lateinit var getData : String

    var mealName : String = ""
    lateinit var mealCategory: String
    lateinit var mealArea: String
    lateinit var mealInstructions: String
    lateinit var mealIngredients: String
    var mealThumbnail: String = ""
    lateinit var mealTags: String
    lateinit var mealYoutube: String
    var stb = StringBuilder()
    var newStb = StringBuilder()
    var returnData : String = ""
    var mealsArray = JSONArray()
    var json = JSONObject()



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrieve_movie_from_api)

        val db = Room.databaseBuilder(this, MealsDatabase::class.java, "Meals_Database").build()
        mealsDao = db.mealDao()

        val getMealBtn = findViewById<Button>(R.id.btnGetMeal)
        //val saveMeal = findViewById<Button>(R.id.btnSaveMeal)
        val inputMeal = findViewById<TextView>(R.id.searchBar)

        //Button to get data
        getMealBtn.setOnClickListener {
            getData = inputMeal.text.toString()
            Toast.makeText(applicationContext,"Retrieve", Toast.LENGTH_SHORT).show()
            callAPI()
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val inputMeal = findViewById<TextView>(R.id.searchBar)
        // Save the current value of the EditText
        outState.putString("editTextValue", inputMeal.text.toString())
        outState.putString("results",returnData)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val inputMeal = findViewById<TextView>(R.id.searchBar)
        val viewMeals = findViewById<TextView>(R.id.viewMeal)
        // Restore the previous value of the EditText
        inputMeal.text = savedInstanceState.getString("editTextValue")
        viewMeals.text = savedInstanceState.getString("results")
    }

    private fun callAPI() {

        var data = ""
        val viewMeals = findViewById<TextView>(R.id.viewMeal)
        viewMeals.movementMethod = ScrollingMovementMethod()

        //API call to get the meals with the entered string
        val url2String = "https://www.themealdb.com/api/json/v1/1/search.php?f=$getData"
        val url = URL(url2String)
        val con: HttpURLConnection = url.openConnection() as HttpURLConnection
        runBlocking {
            launch {
                withContext(Dispatchers.IO){
                    var bf = BufferedReader(InputStreamReader(con.inputStream))
                    var line: String? = bf.readLine()
                    while (line != null){
                        stb.append(line + "\n")
                        line = bf.readLine()
                    }
                    Log.d("STB", stb.toString())
                    parseJSON(stb)
                }
            }
        }
        //Assign the created string to the text view
        viewMeals.text = returnData

    }

    private fun parseJSON(stb: StringBuilder) : String {

        var newLine = ""
        var ingredientLine = ""
        json = JSONObject(stb.toString())
        val error = "{\"meals\":null}"
        Log.d("ERROR", json.toString())
        if (json.toString() == error){
            returnData = "INVALID SEARCH CRITERIA"
            return returnData
        }
        mealsArray = json.getJSONArray("meals")
        //var jsonArray = JSONArray(mealsArray)
        for(i in 0 until mealsArray.length()){
            val mealObject = mealsArray.getJSONObject(i)
            //Log.d("JSON", mealObject.toString())
            if(json.has("Error")){
                returnData = "ERROR!"
            } else {
                //Assign the data to the global vars
                mealName = mealObject.getString("strMeal")
                mealName = mealObject.getString("strMeal")
                mealCategory = mealObject.getString("strCategory")
                mealArea = mealObject.getString("strArea")
                mealInstructions = mealObject.getString("strInstructions")
                mealTags = mealObject.getString("strTags")
                mealYoutube = mealObject.getString("strYoutube")
                for (i in 1..20){
                    var ingredientName = mealObject.getString("strIngredient$i")
                    if (ingredientName != ""){
                        ingredientLine = "$ingredientLine$ingredientName,"
                    }
                }
                mealIngredients = ingredientLine
                //Create string
                newLine = "$newLine\nMeal: $mealName\nCategory: $mealCategory\nArea: $mealArea\nInstructions: $mealInstructions\nIngredients: $mealIngredients\nTags: $mealTags\nYoutube: $mealYoutube"
                newLine = "$newLine\n*************************************************"
                returnData = "Meals:$newLine\n"
            }
        }
        Log.d("Data",returnData)
        return returnData

    }
}