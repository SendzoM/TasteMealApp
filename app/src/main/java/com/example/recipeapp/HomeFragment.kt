package com.example.recipeapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
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

class HomeFragment : Fragment() {

    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var recipeAdapter: RecipeAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recipe_list, container, false)
        recyclerView = view.findViewById(R.id.recipe_recycler_view)
        progressBar = view.findViewById(R.id.recipe_progress_bar)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = RetrofitInstance.api
        val recipeRepository = RecipeRepository(apiService)
        val factory = RecipeViewModelFactory(recipeRepository)

        recipeViewModel = ViewModelProvider(this, factory).get(RecipeViewModel::class.java)

        // Initialize the adapter with an empty list first
        recipeAdapter = RecipeAdapter(emptyList())
        val gridLayoutManager = GridLayoutManager(context, 2)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = recipeAdapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemCount = gridLayoutManager.childCount
                val totalItemCount = gridLayoutManager.itemCount
                val firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition()

                // Check if we're near the end of the list
                if (!recipeViewModel.isLoading.value!! && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    // We've reached the end, load more recipes!
                    recipeViewModel.loadRecipes()
                }
            }
        })
        // Observe recipes LiveData with explicit type
        recipeViewModel.recipes.observe(viewLifecycleOwner, Observer { recipes: List<Recipe> ->
            recipeAdapter.updateRecipes(recipes) // A new method we will add to the adapter
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