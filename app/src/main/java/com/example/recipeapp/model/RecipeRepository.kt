package com.example.recipeapp.model

import com.example.recipeapp.network.SpoonacularApiService

class RecipeRepository(private val apiService: SpoonacularApiService) {

    suspend fun getRecipes(): List<Recipe> {
        return try {
            val response = apiService.getRandomRecipes(number = 10)
            response.recipes
        } catch (e: Exception) {

            println("Error fetching recipes: ${e.message}")
            emptyList()
        }
    }
}