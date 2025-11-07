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
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.recipeapp.viewmodel.RecipeViewModel

class SettingsFragment : Fragment() {

    private lateinit var categorySpinner: Spinner
    private lateinit var allergiesEditText: EditText
    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var languageRadioGroup: RadioGroup
    private lateinit var englishRadioButton: RadioButton
    private lateinit var zuluRadioButton: RadioButton

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

        // Initialize language controls
        languageRadioGroup = view.findViewById(R.id.language_radio_group)
        englishRadioButton = view.findViewById(R.id.english_radio_button)
        zuluRadioButton = view.findViewById(R.id.zulu_radio_button)

        // Setup the dropdown menu
        setupSpinner()
        loadSettings()

        applyButton.setOnClickListener {
            saveSettings() // Save all settings
            recipeViewModel.loadRecipes(categorySpinner.selectedItem.toString().lowercase())
            findNavController().navigate(R.id.nav_home)
        }

        // Listener for language changes
        languageRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedLanguage = when (checkedId) {
                R.id.zulu_radio_button -> "zu"
                else -> "en"
            }
            LocaleHelper.setLocale(requireContext(), selectedLanguage)

            // Restart the activity to apply the new language
            requireActivity().recreate()
        }

        return view
    }

    private fun loadSettings() {
        val sharedPref = activity?.getSharedPreferences("RecipeAppPrefs", Context.MODE_PRIVATE) ?: return
        val savedAllergies = sharedPref.getString("USER_ALLERGIES", "")
        allergiesEditText.setText(savedAllergies)

        // Set the correct radio button for the language
        val currentLang =
            AppCompatDelegate.getApplicationLocales().toLanguageTags()
        if (currentLang.startsWith("zu")) {
            zuluRadioButton.isChecked = true
        } else {
            englishRadioButton.isChecked = true
        }
    }

    private fun saveSettings() {
        val selectedCategory = categorySpinner.selectedItem.toString().lowercase()
        val allergies = allergiesEditText.text.toString()

        val sharedPref =
            activity?.getSharedPreferences("RecipeAppPrefs", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("SELECTED_CATEGORY", selectedCategory)
            putString("USER_ALLERGIES", allergies)
            apply()
        }
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