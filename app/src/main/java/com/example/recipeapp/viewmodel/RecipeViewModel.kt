package com.example.recipeapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.model.Recipe
import com.example.recipeapp.model.RecipeRepository
import kotlinx.coroutines.launch


class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> = _recipes

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var currentPage = 1
    private var isFetching = false

    init {
        loadRecipes()
    }

    fun loadRecipes() {
        // Prevent multiple simultaneous requests
        if (isFetching) return
        isFetching = true

        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val newRecipes = repository.getRecipes(currentPage)
                if (newRecipes.isNotEmpty()) {
                    // Get the current list, or an empty list if it's the first time
                    val currentList = _recipes.value ?: emptyList()
                    // Add the new recipes to the existing list
                    _recipes.postValue(currentList + newRecipes)
                    currentPage++ // Increment the page for the next request
                }
                } catch (e: Exception) {
                    _errorMessage.postValue("Failed to load recipes: ${e.message}")
                } finally {
                    _isLoading.postValue(false)
                    isFetching = false // Allow new requests
                }
            }
        }
    }