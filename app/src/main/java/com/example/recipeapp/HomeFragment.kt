package com.example.recipeapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.adapter.RecipeAdapter
import com.example.recipeapp.model.Recipe
import com.example.recipeapp.model.RecipeRepository
import com.example.recipeapp.network.RetrofitInstance
import com.example.recipeapp.viewmodel.RecipeViewModel
import com.example.recipeapp.viewmodel.RecipeViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var searchView: SearchView


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.home_recipe_recycler_view)
        progressBar = view.findViewById(R.id.home_progress_bar)
        searchView = view.findViewById(R.id.search_view)
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

        // Initialize the adapter with an empty list first
        recipeAdapter = RecipeAdapter(emptyList())
        val gridLayoutManager = GridLayoutManager(context, 2)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = recipeAdapter

        val sharedPref = activity?.getSharedPreferences("RecipeAppPrefs", Context.MODE_PRIVATE)
        val savedCategory = sharedPref?.getString("SELECTED_CATEGORY", "random") ?: "random"

        recipeViewModel.recipes.observe(viewLifecycleOwner, Observer { recipes ->
            recipeAdapter.updateRecipes(recipes ?: emptyList())
        })

        if (recipeViewModel.recipes.value.isNullOrEmpty() && searchView.query.isNullOrBlank()) {
            recipeViewModel.loadRecipes(savedCategory)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // This is called when the user hits the search button
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    recipeViewModel.searchRecipes(query)
                    searchView.clearFocus() // Hide the keyboard
                }
                return true
            }

            // This is called for every character change
            override fun onQueryTextChange(newText: String?): Boolean {
                // If the search text is cleared, reload the category recipes
                if (newText.isNullOrBlank()) {
                        recipeViewModel.loadRecipes(savedCategory)
                }
                return true
            }
        })

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = gridLayoutManager.childCount
                val totalItemCount = gridLayoutManager.itemCount
                val firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition()

                val isLoading = recipeViewModel.isLoading.value ?: false
                if (searchView.query.isNullOrBlank() && !isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    recipeViewModel.loadRecipes(savedCategory)
                }
            }
        })

        recipeViewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessage: String ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        })

        recipeViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading: Boolean ->
            // Only show the big progress bar if the list is empty
            if (isLoading && recipeAdapter.itemCount == 0) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        })
    }

}