package com.example.recipeapp.model

import com.example.recipeapp.network.SpoonacularApiService

class RecipeRepository(private val apiService: SpoonacularApiService) {

    suspend fun getRecipes(page: Int, tags: String?): List<Recipe> {
        return try {
            val response = apiService.getRandomRecipes(number = 10, page = page, tags = tags)
            response.recipes
        } catch (e: Exception) {
            println("Error fetching recipes: ${e.message}")
            emptyList()
        }
    }

    suspend fun searchRecipes(query: String): List<Recipe> {
        return try {
            // Using complexSearch which returns a RecipeResponse
            val response = apiService.searchRecipes(query = query, number = 20) // Fetch 20 results
            response.results
        } catch (e: Exception) {
            println("Error searching recipes: ${e.message}")
            emptyList()
        }
    }
    suspend fun getRecipeDetails(id: Int): Recipe? {
        return try {
            apiService.getRecipeInformation(id)
        } catch (e: Exception) {
            println("Error fetching recipe details: ${e.message}")
            null
        }
    }
}