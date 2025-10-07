package com.example.recipeapp.model

import com.example.recipeapp.network.SpoonacularApiService

class RecipeRepository(private val apiService: SpoonacularApiService) {

    suspend fun getRecipes(page: Int): List<Recipe> {
        return try {
            val response = apiService.getRandomRecipes(number = 10, page = page)
            response.recipes
        } catch (e: Exception) {
            println("Error fetching recipes: ${e.message}")
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