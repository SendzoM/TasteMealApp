package com.example.recipeapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.adapter.RecipeAdapter
import com.example.recipeapp.model.RecipeRepository
import com.example.recipeapp.network.RetrofitInstance
import com.example.recipeapp.viewmodel.RecipeViewModel
import com.example.recipeapp.viewmodel.RecipeViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SavedRecipeFragment : Fragment() {

    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var noRecipesTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_saved_recipe, container, false)
        recyclerView = view.findViewById(R.id.saved_recipe_recycler_view)
        progressBar = view.findViewById(R.id.saved_recipe_progress_bar)
        noRecipesTextView = view.findViewById(R.id.no_saved_recipes_text)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = RetrofitInstance.api
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val recipeRepository = RecipeRepository(apiService, firestore, auth)
        val factory = RecipeViewModelFactory(recipeRepository)
        recipeViewModel = ViewModelProvider(requireActivity(), factory).get(RecipeViewModel::class.java)

        setupRecyclerView()

        recipeViewModel.savedRecipes.observe(viewLifecycleOwner, Observer { recipes ->
            if (recipes.isNullOrEmpty()) {
                noRecipesTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                noRecipesTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            recipeAdapter.updateRecipes(recipes)
        })

        recipeViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading && recipeAdapter.itemCount == 0) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        })

        recipeViewModel.errorMessage.observe(viewLifecycleOwner, Observer { error ->
            if (error != null && error.isNotEmpty()) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Fetch saved recipes every time the user comes to this screen
        // to ensure the list is always up-to-date.
        recipeViewModel.fetchSavedRecipes()
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(emptyList())
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = recipeAdapter
    }

}