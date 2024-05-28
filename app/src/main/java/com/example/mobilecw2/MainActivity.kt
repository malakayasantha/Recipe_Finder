package com.example.mobilecw2

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.room.Room
import com.example.mobilecw2.entities.Ingredient
import com.example.mobilecw2.entities.Meal
import com.example.mobilecw2.entities.relations.MealIngredientCrossReference
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// google drive link : https://drive.google.com/file/d/1xPKDNJamvAEDNdZ5gdyns-vbU-kp9zER/view?usp=share_link

class MainActivity : AppCompatActivity() {

    private lateinit var mealsDao: MealDao

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Build database
        val db = Room.databaseBuilder(this, MealsDatabase::class.java, "Meals_Database").build()
        mealsDao = db.mealDao()

        //Access all the button and views in the activity
        val addToDB = findViewById<Button>(R.id.btnAddDB)
        val searchIngredient = findViewById<Button>(R.id.btnIngredient)
        val searchMeal = findViewById<Button>(R.id.btnMeal)
        val searchWeb = findViewById<Button>(R.id.btnSearchWeb)

        //Add functionality to each button click

        addToDB.setOnClickListener {
            Toast.makeText(applicationContext, "Added Meals Database", Toast.LENGTH_SHORT).show()
            //Created SQL Lite database and add the test data
            enterData()
        }

        searchIngredient.setOnClickListener {
            val searchIngredientIntent = Intent(this, SearchByIngredient::class.java)
            //Search the DB by ingredient name or meal name
            startActivity(searchIngredientIntent)
        }

        searchMeal.setOnClickListener {
            val searchMealIntent = Intent(this, RetrieveAndSaveMeal::class.java)
            //Search the API by the main ingredient and retrieve and save the data in the DB
            startActivity(searchMealIntent)
        }

        searchWeb.setOnClickListener {
            val searchWebIntent = Intent(this, RetrieveMealFromAPI::class.java)
            //Search the API for all the meals with a letter or a part
            startActivity(searchWebIntent)
        }
    }


    //Function to add test data
    private fun enterData() {
        runBlocking {
            launch{
                val meals = listOf(
                    Meal(
                        "Sweet and Sour Pork",
                        "Pork",
                        "Chinese",
                        "Preparation\\r\\n1. Crack the egg into a bowl. Separate the egg white and yolk.\\r\\n\\r\\nSweet and Sour Pork\\r\\n2. Slice the pork tenderloin into ips.\\r\\n\\r\\n3. Prepare the marinade using a pinch of salt, one teaspoon of starch, two teaspoons of light soy sauce, and an egg white.\\r\\n\\r\\n4. Marinade the pork ips for about 20 minutes.\\r\\n\\r\\n5. Put the remaining starch in a bowl. Add some water and vinegar to make a starchy sauce.\\r\\n\\r\\nSweet and Sour Pork\\r\\nCooking Inuctions\\r\\n1. Pour the cooking oil into a wok and heat to 190\\u00b0C (375\\u00b0F). Add the marinated pork ips and fry them until they turn brown. Remove the cooked pork from the wok and place on a plate.\\r\\n\\r\\n2. Leave some oil in the wok. Put the tomato sauce and white sugar into the wok, and heat until the oil and sauce are fully combined.\\r\\n\\r\\n3. Add some water to the wok and thoroughly heat the sweet and sour sauce before adding the pork ips to it.\\r\\n\\r\\n4. Pour in the starchy sauce. Stir-fry all the ingredients until the pork and sauce are thoroughly mixed together.\\r\\n\\r\\n5. Serve on a plate and add some coriander for decoration.",
                        "https:\\/\\/www.themealdb.com\\/images\\/media\\/meals\\/1529442316.jpg",
                        "Sweet",
                        "https:\\/\\/www.youtube.com\\/watch?v=mdaBIhgEAMo",
                        "Pork, Egg, Water, Salt, Sugar, Soy Sauce, Starch, Tomato Puree, Vinegar, Coriander"
                    ),
                    Meal(
                        "Chicken Marengo",
                        "Chicken",
                        "French",
                        "Heat the oil in a large flameproof casserole dish and stir-fry the mushrooms until they start to soften. Add the chicken legs and cook briefly on each side to colour them a little.\\r\\nPour in the passata, crumble in the stock cube and stir in the olives. Season with black pepper \\u2013 you shouldn\\u2019t need salt. Cover and simmer for 40 mins until the chicken is tender. Sprinkle with parsley and serve with pasta and a salad, or mash and green veg, if you like.",
                        "https:\\/\\/www.themealdb.com\\/images\\/media\\/meals\\/qpxvuq1511798906.jpg",
                        null,
                        "https:\\/\\/www.youtube.com\\/watch?v=U33HYUr-0Fw",
                        "Olive Oil, Mushrooms, Chicken Legs, Passata, Chicken Stock Cube, Black Olives, Parsley"
                    ),
                    Meal(
                            "Beef Banh Mi Bowls with Sriracha Mayo, Carrot & Pickled Cucumber",
                            "Beef",
                            "Vietnamese",
                            "Add'l ingredients: mayonnaise, siracha\\r\\n\\r\\n1\\r\\n\\r\\nPlace rice in a fine-mesh sieve and rinse until water runs clear. Add to a small pot with 1 cup water (2 cups for 4 servings) and a pinch of salt. Bring to a boil, then cover and reduce heat to low. Cook until rice is tender, 15 minutes. Keep covered off heat for at least 10 minutes or until ready to serve.\\r\\n\\r\\n2\\r\\n\\r\\nMeanwhile, wash and dry all produce. Peel and finely chop garlic. Zest and quarter lime (for 4 servings, zest 1 lime and quarter both). Trim and halve cucumber lengthwise; thinly slice crosswise into half-moons. Halve, peel, and medium dice onion. Trim, peel, and grate carrot.\\r\\n\\r\\n3\\r\\n\\r\\nIn a medium bowl, combine cucumber, juice from half the lime, \\u00bc tsp sugar (\\u00bd tsp for 4 servings), and a pinch of salt. In a small bowl, combine mayonnaise, a pinch of garlic, a squeeze of lime juice, and as much sriracha as you\\u2019d like. Season with salt and pepper.\\r\\n\\r\\n4\\r\\n\\r\\nHeat a drizzle of oil in a large pan over medium-high heat. Add onion and cook, stirring, until softened, 4-5 minutes. Add beef, remaining garlic, and 2 tsp sugar (4 tsp for 4 servings). Cook, breaking up meat into pieces, until beef is browned and cooked through, 4-5 minutes. Stir in soy sauce. Turn off heat; taste and season with salt and pepper.\\r\\n\\r\\n5\\r\\n\\r\\nFluff rice with a fork; stir in lime zest and 1 TBSP butter. Divide rice between bowls. Arrange beef, grated carrot, and pickled cucumber on top. Top with a squeeze of lime juice. Drizzle with sriracha mayo.",
                            "https:\\/\\/www.themealdb.com\\/images\\/media\\/meals\\/z0ageb1583189517.jpg",
                            null,
                            "",
                            "Rice, Onion, Lime, Garlic Clove, Cucumber, Carrots, Ground Beef, Soy Sauce"
                    ),
                    Meal(
                        "Leblebi Soup",
                        "Vegetarian",
                        "Tunisian",
                        "Heat the oil in a large pot. Add the onion and cook until translucent.\\r\\nDrain the soaked chickpeas and add them to the pot together with the vegetable stock. Bring to the boil, then reduce the heat and cover. Simmer for 30 minutes.\\r\\nIn the meantime toast the cumin in a small ungreased frying pan, then grind them in a mortar. Add the garlic and salt and pound to a fine paste.\\r\\nAdd the paste and the harissa to the soup and simmer until the chickpeas are tender, about 30 minutes.\\r\\nSeason to taste with salt, pepper and lemon juice and serve hot.",
                        "https:\\/\\/www.themealdb.com\\/images\\/media\\/meals\\/x2fw9e1560460636.jpg",
                        "Soup",
                        "https:\\/\\/www.youtube.com\\/watch?v=BgRifcCwinY",
                        "Olive Oil, Onion, Chickpeas, Vegetable Stock, Cumin, Garlic, Salt, Harissa Spice, Pepper, Lime"
                    )
                )

                val ingredients = listOf(
                    Ingredient("Pork"),
                    Ingredient("Egg"),
                    Ingredient("Water"),
                    Ingredient("Salt"),
                    Ingredient("Sugar"),
                    Ingredient("Soy Sauce"),
                    Ingredient("Starch"),
                    Ingredient("Tomato Puree"),
                    Ingredient("Vinegar"),
                    Ingredient("Coriander"),
                    Ingredient("Olive Oil"),
                    Ingredient("Mushrooms"),
                    Ingredient("Chicken Legs"),
                    Ingredient("Passata"),
                    Ingredient("Chicken Stock Cube"),
                    Ingredient("Black Olives"),
                    Ingredient("Parsley"),
                    Ingredient("Rice"),
                    Ingredient("Onion"),
                    Ingredient("Lime"),
                    Ingredient("Garlic"),
                    Ingredient("Cucumber"),
                    Ingredient("Carrots"),
                    Ingredient("Ground Beef"),
                    Ingredient("Vegetable Stock"),
                    Ingredient("Cumin"),
                    Ingredient("Harissa Spice"),
                    Ingredient("Pepper"),
                    Ingredient("")
                )



                val mealIngredientMeasurementRelationships = listOf(
                    MealIngredientCrossReference(
                        "Sweet and Sour Pork",
                        "Pork",

                    ),
                    MealIngredientCrossReference(
                        "Sweet and Sour Pork",
                        "Egg",

                    ),
                    MealIngredientCrossReference(
                        "Sweet and Sour Pork",
                        "Water",

                    ),
                    MealIngredientCrossReference(
                        "Sweet and Sour Pork",
                        "Salt",
                    ),
                    MealIngredientCrossReference(
                        "Sweet and Sour Pork",
                        "Sugar",
                    ),
                    MealIngredientCrossReference(
                        "Sweet and Sour Pork",
                        "Soy Sauce",
                    ),
                    MealIngredientCrossReference(
                        "Sweet and Sour Pork",
                        "Starch",
                    ),
                    MealIngredientCrossReference(
                        "Sweet and Sour Pork",
                        "Tomato Puree",
                    ),
                    MealIngredientCrossReference(
                        "Sweet and Sour Pork",
                        "Vinegar",
                    ),
                    MealIngredientCrossReference(
                        "Sweet and Sour Pork",
                        "Coriander",
                    ),
                    MealIngredientCrossReference(
                        "Chicken Marengo",
                        "Olive Oil",

                    ),
                    MealIngredientCrossReference(
                        "Chicken Marengo",
                        "Mushrooms",

                    ),
                    MealIngredientCrossReference(
                        "Chicken Marengo",
                        "Chicken legs",

                    ),
                    MealIngredientCrossReference(
                        "Chicken Marengo",
                        "Passata",

                    ),
                    MealIngredientCrossReference(
                        "Chicken Marengo",
                        "Chicken Stock Cube",

                    ),
                    MealIngredientCrossReference(
                        "Chicken Marengo",
                        "Black Olives",

                    ),
                    MealIngredientCrossReference(
                        "Chicken Marengo",
                        "Parsley",

                    ),
                    MealIngredientCrossReference(
                        "Beef Banh Mi Bowls with Sriracha Mayo, Carrot & Pickled Cucumber",
                        "Rice",

                    ),
                    MealIngredientCrossReference(
                        "Beef Banh Mi Bowls with Sriracha Mayo, Carrot & Pickled Cucumber",
                        "Onion",

                    ),
                    MealIngredientCrossReference(
                        "Beef Banh Mi Bowls with Sriracha Mayo, Carrot & Pickled Cucumber",
                        "Lime",

                    ),
                    MealIngredientCrossReference(
                        "Beef Banh Mi Bowls with Sriracha Mayo, Carrot & Pickled Cucumber",
                        "Garlic Clove",

                    ),
                    MealIngredientCrossReference(
                        "Beef Banh Mi Bowls with Sriracha Mayo, Carrot & Pickled Cucumber",
                        "Cucumber",

                    ),
                    MealIngredientCrossReference(
                        "Beef Banh Mi Bowls with Sriracha Mayo, Carrot & Pickled Cucumber",
                        "Carrots",

                    ),
                    MealIngredientCrossReference(
                        "Beef Banh Mi Bowls with Sriracha Mayo, Carrot & Pickled Cucumber",
                        "Ground Beef",

                    ),
                    MealIngredientCrossReference(
                        "Beef Banh Mi Bowls with Sriracha Mayo, Carrot & Pickled Cucumber",
                        "Soy Sauce",

                    ),
                    MealIngredientCrossReference(
                        "Leblebi Soup",
                        "Olive Oil",

                    ),
                    MealIngredientCrossReference(
                        "Leblebi Soup",
                        "Onion",

                    ),
                    MealIngredientCrossReference(
                        "Leblebi Soup",
                        "Chickpeas",

                    ),
                    MealIngredientCrossReference(
                        "Leblebi Soup",
                        "Vegetable Stock",

                    ),
                    MealIngredientCrossReference(
                        "Leblebi Soup",
                        "Cumin",

                    ),
                    MealIngredientCrossReference(
                        "Leblebi Soup",
                        "Garlic",

                    ),
                    MealIngredientCrossReference(
                        "Leblebi Soup",
                        "Salt",

                    ),
                    MealIngredientCrossReference(
                        "Leblebi Soup",
                        "Harissa Spice",

                    ),
                    MealIngredientCrossReference(
                        "Leblebi Soup",
                        "Pepper",

                    ),
                    MealIngredientCrossReference(
                        "Leblebi Soup",
                        "Lime",

                    ),
                )

                for (meal in meals){
                    mealsDao.insertMeal(meal)
                }

                for (ingredient in ingredients){
                    mealsDao.insertIngredient(ingredient)
                }

                for (mealIngredientCrossRef in mealIngredientMeasurementRelationships){
                    mealsDao.insertCrossRef(mealIngredientCrossRef)
                }

                val meal: List<Meal> = mealsDao.getMeal()

                for (meal in meal){
                    Log.d("insert","$meal")
                }
            }
        }
    }
}