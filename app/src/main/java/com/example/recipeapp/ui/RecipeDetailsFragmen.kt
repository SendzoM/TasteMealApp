package com.example.recipeapp.ui

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.model.Recipe
import com.example.recipeapp.model.RecipeRepository
import com.example.recipeapp.network.RetrofitInstance
import com.example.recipeapp.viewmodel.RecipeViewModel
import com.example.recipeapp.viewmodel.RecipeViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RecipeDetailsFragment : Fragment() {

    private lateinit var recipeViewModel: RecipeViewModel
    private var currentRecipe: Recipe? = null // Store the current recipe
    private lateinit var saveFab: FloatingActionButton // Reference to the FAB

    private var recipeId: Int = -1
    private lateinit var passedRecipe: Recipe // To hold the recipe passed via navigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get the recipe ID and basic info from the navigation arguments
        arguments?.let {
            val safeArgs = RecipeDetailsFragmentArgs.fromBundle(it)
            passedRecipe = safeArgs.recipe
            recipeId = passedRecipe.id
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recipe_details, container, false)
        saveFab = view.findViewById(R.id.fab_save_recipe) // Initialize the FAB
        return view    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = RetrofitInstance.api
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val recipeRepository = RecipeRepository(apiService, firestore, auth)
        val factory = RecipeViewModelFactory(recipeRepository)
        recipeViewModel = ViewModelProvider(requireActivity(), factory).get(RecipeViewModel::class.java)

        // Immediately populate the UI with the recipe that was passed to the fragment.
        populateUi(passedRecipe)
        //Set the currentRecipe so the save button works instantly.
        currentRecipe = passedRecipe

        // Observe the detailed recipe LiveData
        recipeViewModel.recipeDetails.observe(viewLifecycleOwner, Observer { detailedRecipe ->
            detailedRecipe?.let {
                currentRecipe = it
                populateUi(it)
            }
        })

        // Observe the saved status from the ViewModel
        recipeViewModel.isRecipeSaved.observe(viewLifecycleOwner, Observer { isSaved ->
            updateFabIcon(isSaved)
        })

        // Set up the FAB click listener
        saveFab.setOnClickListener {
            currentRecipe?.let { recipeToSave ->
                recipeViewModel.toggleSaveRecipe(recipeToSave)
                val willBeSaved = !(recipeViewModel.isRecipeSaved.value ?: false)
                val message = if (willBeSaved) "Recipe Saved" else "Recipe Unsaved"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }

        // Fetch details and check saved state
        if (recipeId != -1) {
            recipeViewModel.fetchRecipeDetails(recipeId)
            recipeViewModel.checkIfRecipeIsSaved(recipeId) // Check if it's already saved
        }
    }

    private fun updateFabIcon(isSaved: Boolean) {
        if (isSaved) {
            saveFab.setImageResource(R.drawable.baseline_bookmark_filled) // Filled icon
        } else {
            saveFab.setImageResource(R.drawable.baseline_save) // Border icon
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun populateUi(recipe: Recipe) {
        view?.apply {
            findViewById<TextView>(R.id.recipe_title).text = recipe.title
            val summaryHtml = recipe.summary ?: "No summary available."
            findViewById<TextView>(R.id.recipe_summary).text = Html.fromHtml(summaryHtml, Html.FROM_HTML_MODE_COMPACT)

            val imageView = findViewById<ImageView>(R.id.recipe_image)
            Glide.with(requireContext()).load(recipe.image).into(imageView)

            // Format and display ingredients
            val ingredientsText = recipe.extendedIngredients?.joinToString(separator = "\n") { "- ${it.original}" } ?: "No ingredients available."
            findViewById<TextView>(R.id.recipe_ingredients).text = ingredientsText

            val instructionsText = recipe.analyzedInstructions?.firstOrNull()?.steps?.joinToString(separator = "\n") { "${it.number}. ${it.step}" } ?: "No instructions available."
            findViewById<TextView>(R.id.recipe_instructions).text = instructionsText
        }
    }
}