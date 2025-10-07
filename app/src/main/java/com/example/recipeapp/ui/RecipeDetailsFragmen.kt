package com.example.recipeapp.ui

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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

class RecipeDetailsFragment : Fragment() {

    private lateinit var recipeViewModel: RecipeViewModel
    private var recipeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get the recipe ID and basic info from the navigation arguments
        arguments?.let {
            val safeArgs = RecipeDetailsFragmentArgs.fromBundle(it)
            recipeId = safeArgs.recipe.id
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_details, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = RetrofitInstance.api
        val recipeRepository = RecipeRepository(apiService)
        val factory = RecipeViewModelFactory(recipeRepository)
        recipeViewModel = ViewModelProvider(requireActivity(), factory).get(RecipeViewModel::class.java)

        // Observe the detailed recipe LiveData
        recipeViewModel.recipeDetails.observe(viewLifecycleOwner, Observer { recipe ->
            recipe?.let {
                populateUi(it)
            }
        })

        // Fetch the details using the ID from arguments
        if (recipeId != -1) {
            recipeViewModel.fetchRecipeDetails(recipeId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun populateUi(recipe: Recipe) {
        view?.apply {
            findViewById<TextView>(R.id.recipe_title).text = recipe.title
            findViewById<TextView>(R.id.recipe_summary).text = Html.fromHtml(recipe.summary, Html.FROM_HTML_MODE_COMPACT)

            val imageView = findViewById<ImageView>(R.id.recipe_image)
            Glide.with(requireContext()).load(recipe.image).into(imageView)

            // --- POPULATE NEW FIELDS ---
            // Format and display ingredients
            val ingredientsTextView = findViewById<TextView>(R.id.recipe_ingredients)
            val ingredientsText = recipe.extendedIngredients?.joinToString(separator = "\n") { "- ${it.original}" } ?: "No ingredients available."
            ingredientsTextView.text = ingredientsText

            // Format and display instructions
            val instructionsTextView = findViewById<TextView>(R.id.recipe_instructions)
            val instructionsText = recipe.analyzedInstructions?.firstOrNull()?.steps?.joinToString(separator = "\n") { "${it.number}. ${it.step}" } ?: "No instructions available."
            instructionsTextView.text = instructionsText
        }
    }
}