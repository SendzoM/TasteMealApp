package com.example.recipeapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.model.Recipe
import com.example.recipeapp.model.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.ViewModelProvider

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> = _recipes

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadRecipes()
    }

    fun loadRecipes() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val recipeList = withContext(Dispatchers.IO) {
                    repository.getRecipes()
                }
                _recipes.value = recipeList
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching recipes: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}