package com.example.mobilecw2

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Website.URL
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.room.Room
import com.example.mobilecw2.entities.Meal
import com.example.mobilecw2.entities.relations.IngredientsWithMeals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.*

class SearchByIngredient : AppCompatActivity() {

    private lateinit var mealDao: MealDao
    private lateinit var listOfMeals : List<IngredientsWithMeals>
    private lateinit var searchedMeal : List<Meal>
    private lateinit var ingredientInput : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_by_ingredient)

        val searchBtn = findViewById<Button>(R.id.btnSearch)
        val getIngredient = findViewById<TextView>(R.id.txtInput)
        val db = Room.databaseBuilder(this, MealsDatabase::class.java, "Meals_Database").build()
        mealDao = db.mealDao()

        //Search DB by ingredient or meal name
        searchBtn.setOnClickListener {
            ingredientInput = getIngredient.text.toString()
            Log.d("Input",ingredientInput)
            searchMealsWithIngredient(ingredientInput)

        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val getIngredient = findViewById<TextView>(R.id.txtInput)
        val searchedText = findViewById<TextView>(R.id.viewSearch)
        // Save the current value of the EditText
        outState.putString("editTextValue", getIngredient.text.toString())
        outState.putString("searchedResults",searchedText.text.toString())
        if (searchedMeal.isNotEmpty()){
            outState.putString("imageUrl", searchedMeal[0].mealThumbnail)
        }

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val getIngredient = findViewById<TextView>(R.id.txtInput)
        val viewSearchedIngredient = findViewById<TextView>(R.id.viewSearch)
        val imageView = findViewById<ImageView>(R.id.imageView)
        // Restore the previous value of the EditText
        getIngredient.text = savedInstanceState.getString("editTextValue")
        viewSearchedIngredient.text = savedInstanceState.getString("searchedResults")
        // Retrieve the URL from savedInstanceState
        val imageUrl = savedInstanceState.getString("imageUrl")

        // Load the image from URL and set it to ImageView using AsyncTask
        if (imageUrl != null) {
            object : AsyncTask<String, Void, Bitmap>() {

                @SuppressLint("StaticFieldLeak")
                override fun doInBackground(vararg params: String?): Bitmap? {
                    val url = params[0]
                    var bitmap: Bitmap? = null
                    try {
                        val inputStream = URL(url).openStream()
                        bitmap = BitmapFactory.decodeStream(inputStream)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return bitmap
                }

                override fun onPostExecute(result: Bitmap?) {
                    if (result != null) {
                        imageView.setImageBitmap(result)
                    }
                }
            }.execute(imageUrl)
        }

    }

    private fun searchMealsWithIngredient(query: String){

        val viewSearchedIngredient = findViewById<TextView>(R.id.viewSearch)
        var mealNames = ""
        var ingredientNames = ""
        viewSearchedIngredient.movementMethod = ScrollingMovementMethod()

        runBlocking {
            launch {
                withContext(Dispatchers.IO){
                    //Search the DB if the entered values is an ingredient
                    listOfMeals = mealDao.getIngredientsWithMeals(query)
                    val sizeList = listOfMeals.size
                    Log.d("Meals","$listOfMeals")
                    if (listOfMeals.isEmpty()){
                        Log.d("TEST", listOfMeals.toString())

                        //If not check if it is a meal
                        searchedMeal = mealDao.searchMeal(query)
                        if (searchedMeal.isEmpty()){
                            viewSearchedIngredient.text = "NO DATA FOUND"
                        }else {
                            Log.d("Searched",searchedMeal.toString())
                            //Assign the meal name
                            val mealName = searchedMeal[0].mealName
                            viewSearchedIngredient.text = "Meal Name: $mealName"
                            if (searchedMeal[0].mealThumbnail != "") {
                                //Assign the image thumbnail
                                val imageView = findViewById<ImageView>(R.id.imageView)
                                loadImageFromUrl(searchedMeal[0].mealThumbnail, imageView)
                            }
                        }
                    }
                }

                for (meal in listOfMeals){
                    Log.d("Meal","$meal")
                }

                if(listOfMeals.size > 1){
                    for (loop in listOfMeals){
                        for (meal in loop.meals){
                            val mealName = meal.mealName
                            val ingredientName = loop.ingredients.ingredientName
                            Log.d("Results","$mealName + $ingredientName")
                            //Get the data
                             mealNames = "$mealNames$mealName + $ingredientName\n"
                        }
                    }
                    viewSearchedIngredient.text = mealNames
                } else if(listOfMeals.size == 1){
                    val mealName = listOfMeals[0].meals[0].mealName
                    //Assign the data to the text view
                    viewSearchedIngredient.text = "Meal Name: $mealName"
                }

            }
        }

    }



    private fun loadImageFromUrl(url: String, imageView: ImageView) {
        //Get the image from the URL and using BitmapFactory decode the input stream and assign the image to the image view
        Thread {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(input)
                imageView.post {
                    imageView.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

}



