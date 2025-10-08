package com.example.recipeapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.recipeapp.viewmodel.RecipeViewModel

class SettingsFragment : Fragment() {

    private lateinit var categorySpinner: Spinner
    private lateinit var recipeViewModel: RecipeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        recipeViewModel =
            ViewModelProvider(requireActivity()).get(RecipeViewModel::class.java)
        categorySpinner = view.findViewById(R.id.category_spinner)
        val applyButton = view.findViewById<Button>(R.id.apply_settings_button)

        // Setup the dropdown menu
        setupSpinner()

        applyButton.setOnClickListener {
            // Get selected category
            val selectedCategory = categorySpinner.selectedItem.toString().lowercase()

            // Save the selected category to SharedPreferences
            val sharedPref = activity?.getSharedPreferences("RecipeAppPrefs", Context.MODE_PRIVATE)
                ?: return@setOnClickListener
            with(sharedPref.edit()) {
                putString("SELECTED_CATEGORY", selectedCategory)
                apply()
            }

            recipeViewModel.loadRecipes(selectedCategory)
            // Navigate back to the Home screen to see the new recipes
            findNavController().navigate(R.id.nav_home)
        }

        return view
    }

    private fun setupSpinner() {
        // Define the categories. "Random" will have no tag.
        val categories = arrayOf("Random", "Main Course", "Dessert", "Salad", "Breakfast", "Soup")
        // Create an adapter for the spinner
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

    }
}