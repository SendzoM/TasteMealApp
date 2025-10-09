package com.example.recipeapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.recipeapp.viewmodel.RecipeViewModel

class SettingsFragment : Fragment() {

    private lateinit var categorySpinner: Spinner
    private lateinit var allergiesEditText: EditText
    private lateinit var recipeViewModel: RecipeViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        recipeViewModel =
            ViewModelProvider(requireActivity()).get(RecipeViewModel::class.java)
        categorySpinner = view.findViewById(R.id.category_spinner)
        allergiesEditText = view.findViewById(R.id.allergies_edit_text)
        val applyButton = view.findViewById<Button>(R.id.apply_settings_button)

        // Setup the dropdown menu
        setupSpinner()
        loadSettings()

        applyButton.setOnClickListener {
            // Get selected category
            val selectedCategory = categorySpinner.selectedItem.toString().lowercase()
            val allergies = allergiesEditText.text.toString()

            // Save the selected category to SharedPreferences
            val sharedPref = activity?.getSharedPreferences("RecipeAppPrefs", Context.MODE_PRIVATE)
                ?: return@setOnClickListener
            with(sharedPref.edit()) {
                putString("SELECTED_CATEGORY", selectedCategory)
                putString("USER_ALLERGIES", allergies)
                apply()
            }

            recipeViewModel.loadRecipes(selectedCategory)
            // Navigate back to the Home screen to see the new recipes
            findNavController().navigate(R.id.nav_home)
        }

        return view
    }

    private fun loadSettings() {
        val sharedPref =
            activity?.getSharedPreferences("RecipeAppPrefs", Context.MODE_PRIVATE) ?: return
        val savedAllergies = sharedPref.getString("USER_ALLERGIES", "") // Default to empty string
        allergiesEditText.setText(savedAllergies)
    }

    private fun setupSpinner() {
        // Define the categories. "Random" will have no tag.
        val categories = arrayOf(
            "Random",
            "Main Course",
            "Dessert",
            "Salad",
            "Breakfast",
            "Soup",
            "Appetizer",
            "Side Dish",
            "Snack",
            "Drink",
            "Italian",
            "Mexican",
            "Indian",
            "Chinese",
            "American",
            "African",
            "Japanese",
            "Vegetarian",
            "Vegan")

        // adapter for the spinner
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

    }
}