package com.example.recipeapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.adapter.RecipeAdapter
import com.example.recipeapp.model.Recipe
import com.example.recipeapp.model.RecipeRepository
import com.example.recipeapp.network.RetrofitInstance
import com.example.recipeapp.viewmodel.RecipeViewModel
import com.example.recipeapp.viewmodel.RecipeViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RecipeListFragment : Fragment() {

    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

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
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val recipeRepository = RecipeRepository(apiService, firestore, auth)
        val factory = RecipeViewModelFactory(recipeRepository)

        recipeViewModel = ViewModelProvider(this, factory).get(RecipeViewModel::class.java)

        recyclerView.layoutManager = LinearLayoutManager(context)

        // Observe recipes LiveData with explicit type
        recipeViewModel.recipes.observe(viewLifecycleOwner, Observer { recipes: List<Recipe> ->
            recyclerView.adapter = RecipeAdapter(recipes)
        })

        recipeViewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessage: String ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        })

        recipeViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading: Boolean ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })
    }
}
