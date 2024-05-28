package com.example.mobilecw2

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.mobilecw2.entities.Meal
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

class RetrieveAndSaveMeal : AppCompatActivity() {

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
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrieve_and_save_meal)

        val db = Room.databaseBuilder(this, MealsDatabase::class.java, "Meals_Database").build()
        mealsDao = db.mealDao()

        val getMealBtn = findViewById<Button>(R.id.btnGetMeal)
        val saveMeal = findViewById<Button>(R.id.btnSaveMeal)
        val inputMeal = findViewById<TextView>(R.id.searchBar)

        //Buttons to save and get data from API
        saveMeal.setOnClickListener {
            Toast.makeText(applicationContext, "Saved to meals DB",Toast.LENGTH_SHORT).show()
            saveDataToDB()
        }

        getMealBtn.setOnClickListener {
            getData = inputMeal.text.toString()
            Toast.makeText(applicationContext,"Retrieve",Toast.LENGTH_SHORT).show()
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

    private fun callAPI(){
        var data = ""
        val viewMeals = findViewById<TextView>(R.id.viewMeal)
        viewMeals.movementMethod = ScrollingMovementMethod()

        //Get data from the URL by appending the search criteria
        val url2String = "https://www.themealdb.com/api/json/v1/1/filter.php?c=$getData"
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
        viewMeals.text = returnData
    }


    private fun parseJSON(stb: java.lang.StringBuilder): String{

        var newLine = ""
        var ingredientLine = ""
        json = JSONObject(stb.toString())
        val error = "{\"meals\":null}"
        Log.d("ERROR", json.toString())
        if (json.toString() == error){
            returnData = "INVALID SEARCH CRITERIA"
            return returnData
        }


        //Consists of all the meal names consisting the main ingredient
        mealsArray = json.getJSONArray("meals")

        for(i in 0 until mealsArray.length()){
            val mealObject = mealsArray.getJSONObject(i)
            Log.d("JSON", mealObject.toString())
            if(json.has("Error")){
                returnData = "ERROR!"
            } else {
                mealName = mealObject.getString("strMeal")
                //Another API call in order to get the meal details for each meal which has the main ingredient
                val url2String = "https://www.themealdb.com/api/json/v1/1/search.php?s=$mealName"
                val url = URL(url2String)
                val con: HttpURLConnection = url.openConnection() as HttpURLConnection
                val newStb = StringBuilder() // Initialize newStb for each meal
                runBlocking {
                    launch {
                        withContext(Dispatchers.IO){
                            var bf = BufferedReader(InputStreamReader(con.inputStream))
                            var line: String? = bf.readLine()
                            while (line != null){
                                newStb.append(line + "\n")
                                line = bf.readLine()
                            }
                        }
                    }
                }
                json = JSONObject(newStb.toString())
                val mealsList = json.getJSONArray("meals")
                val mealListObject = mealsList.getJSONObject(0)
                Log.d("Meals List", mealListObject.toString())
                //Appending the meal details to the global variables
                mealName = mealListObject.getString("strMeal")
                mealCategory = mealListObject.getString("strCategory")
                mealArea = mealListObject.getString("strArea")
                mealInstructions = mealListObject.getString("strInstructions")
                mealTags = mealListObject.getString("strTags")
                mealYoutube = mealListObject.getString("strYoutube")
                //As one meal can have up to 20 ingredients loop to check and get the ingredients per that meal
                for (i in 1..20){
                    var ingredientName = mealListObject.getString("strIngredient$i")
                    if (ingredientName != ""){
                        ingredientLine = "$ingredientLine$ingredientName,"
                    }
                }
                mealIngredients = ingredientLine
                ingredientLine = ""
                //Creating the string
                newLine = "$newLine\nMeal: $mealName\nCategory: $mealCategory\nArea: $mealArea\nInstructions: $mealInstructions\nIngredients: $mealIngredients\nTags: $mealTags\nYoutube: $mealYoutube"
            }
            newLine = "$newLine\n*************************************************"
        }
        returnData = "Meals:$newLine\n" // Move this line outside the loop

        //}
        return returnData
    }


    private fun saveDataToDB(){

        //var newLine = ""
        var ingredientLine = ""

        json = JSONObject(stb.toString())
        val error = "{\"meals\":null}"
        Log.d("ERROR", json.toString())
        if (json.toString() == error){
            return
        }

        mealsArray = json.getJSONArray("meals")
        //var jsonArray = JSONArray(mealsArray)
        for(i in 0 until mealsArray.length()){
            val mealObject = mealsArray.getJSONObject(i)
            Log.d("JSON", mealObject.toString())
            if(json.has("Error")){
                returnData = "ERROR!"
            } else {
                mealName = mealObject.getString("strMeal")
                //API call to get all the meal details for the searched main ingredient
                val url2String = "https://www.themealdb.com/api/json/v1/1/search.php?s=$mealName"
                val url = URL(url2String)
                val con: HttpURLConnection = url.openConnection() as HttpURLConnection
                val newStb = StringBuilder() // Initialize newStb for each meal
                runBlocking {
                    launch {
                        withContext(Dispatchers.IO){
                            var bf = BufferedReader(InputStreamReader(con.inputStream))
                            var line: String? = bf.readLine()
                            while (line != null){
                                newStb.append(line + "\n")
                                line = bf.readLine()
                            }
                        }
                    }
                }
                json = JSONObject(newStb.toString())
                val mealsList = json.getJSONArray("meals")
                val mealListObject = mealsList.getJSONObject(0)
                Log.d("Meals List", mealListObject.toString())
                //Assign the meal details to each global variable
                mealName = mealListObject.getString("strMeal")
                mealCategory = mealListObject.getString("strCategory")
                mealArea = mealListObject.getString("strArea")
                mealInstructions = mealListObject.getString("strInstructions")
                mealTags = mealListObject.getString("strTags")
                mealYoutube = mealListObject.getString("strYoutube")
                for (i in 1..20){
                    var ingredientName = mealListObject.getString("strIngredient$i")
                    if (ingredientName != ""){
                        ingredientLine = "$ingredientLine$ingredientName,"
                    }
                }
                mealIngredients = ingredientLine
                ingredientLine = ""
                //Saving the retrieved data to the Database
                runBlocking {
                    launch {
                        val meals = listOf(
                            Meal(mealName,mealCategory,mealArea,mealInstructions,mealThumbnail,mealTags,mealYoutube,mealIngredients)
                        )

                        for (meal in meals){
                            mealsDao.insertMeal(meal)
                        }

                        val mealsList :List<Meal> = mealsDao.getMeal()

                        for (meal in mealsList){
                            Log.d("Meals",meal.toString())
                        }
                    }
                }
            }
        }
    }
}