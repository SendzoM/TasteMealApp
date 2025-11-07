package com.example.recipeapp.viewmodel

import androidx.lifecycle.*
import com.example.recipeapp.model.Recipe
import com.example.recipeapp.model.RecipeRepository
import com.example.recipeapp.model.RecipeSearchResponse
import kotlinx.coroutines.launch

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {

    // Main LiveData for displaying lists of recipes (used for both search and categories)
    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> = _recipes

    // LiveData for the details screen
    private val _recipeDetails = MutableLiveData<Recipe?>()
    val recipeDetails: LiveData<Recipe?> = _recipeDetails

    // LiveData for saved recipes
    private val _savedRecipes = MutableLiveData<List<Recipe>>()
    val savedRecipes: LiveData<List<Recipe>> = _savedRecipes

    // LiveData for UI state management
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRecipeSaved = MutableLiveData<Boolean>()
    val isRecipeSaved: LiveData<Boolean> = _isRecipeSaved

    private var currentPage = 1
    private var isFetching = false
    private var currentCategory: String? = null

    // Enhanced functionality (from second implementation)
    fun loadRecipes(category: String?) {
        viewModelScope.launch {
            // If the category has changed, reset the list and page number
            if (category != currentCategory) {
                currentPage = 1
                _recipes.postValue(emptyList()) // Clear the list on UI
                currentCategory = category
            }
            // Prevent multiple simultaneous requests
            if (isFetching) return@launch
            isFetching = true

            _isLoading.postValue(true)
            try {
                val tags = if (category.equals("random", ignoreCase = true)) null else category
                val newRecipes = repository.getRecipes(currentPage, tags)
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

    fun searchRecipes(query: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            _recipes.postValue(emptyList()) // Clear previous results immediately
            try {
                val searchResults = repository.searchRecipes(query)
                _recipes.postValue(searchResults)
            } catch (e: Exception) {
                _errorMessage.postValue("Search failed: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun fetchRecipeDetails(id: Int) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val details = repository.getRecipeDetails(id)
                _recipeDetails.postValue(details)
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to load details: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun fetchSavedRecipes() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val saved = repository.getSavedRecipes()
                _savedRecipes.postValue(saved)
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to fetch saved recipes: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun toggleSaveRecipe(recipe: Recipe) {
        viewModelScope.launch {
            val isCurrentlySaved = _isRecipeSaved.value ?: false
            try {
                if (isCurrentlySaved) {
                    repository.removeRecipe(recipe.id)
                } else {
                    repository.saveRecipe(recipe)
                }
                _isRecipeSaved.postValue(!isCurrentlySaved)
            } catch (e: Exception) {
                _errorMessage.postValue("Error updating saved state: ${e.message}")
            }
        }
    }

    fun checkIfRecipeIsSaved(recipeId: Int) {
        viewModelScope.launch {
            try {
                val isSaved = repository.isRecipeSaved(recipeId)
                _isRecipeSaved.postValue(isSaved)
            } catch (e: Exception) {
                // Handle error, maybe post a default value
                _isRecipeSaved.postValue(false)
            }
        }
    }

    // Helper method to clear error messages
    fun clearErrorMessage() {
        _errorMessage.value = ""
    }

    /* Reset pagination and clear data
    fun reset() {
        currentPage = 1
        currentCategory = null
        _recipes.value = emptyList()
        _recipeDetails.value = null
        _errorMessage.value = ""
    }*/
}