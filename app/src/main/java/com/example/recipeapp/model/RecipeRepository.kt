package com.example.recipeapp.model

import com.example.recipeapp.network.SpoonacularApiService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RecipeRepository(
    private val apiService: SpoonacularApiService,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private fun getCurrentUserId(): String? = auth.currentUser?.uid
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

    suspend fun saveRecipe(recipe: Recipe) {
        getCurrentUserId()?.let { userId ->
            // Use recipe.id as the document ID for easy checking and deletion
            firestore.collection("users").document(userId)
                .collection("savedRecipes").document(recipe.id.toString())
                .set(recipe) // set() will create or overwrite
                .await() // await() makes it a suspending function
        }
    }

    suspend fun removeRecipe(recipeId: Int) {
        getCurrentUserId()?.let { userId ->
            firestore.collection("users").document(userId)
                .collection("savedRecipes").document(recipeId.toString())
                .delete()
                .await()
        }
    }

    suspend fun getSavedRecipes(): List<Recipe> {
        val userId = getCurrentUserId() ?: return emptyList() // Return empty if no user is logged in
        return try {
            val snapshot = firestore.collection("users").document(userId)
                .collection("savedRecipes")
                .get()
                .await()
            // Convert all documents in the collection to Recipe objects
            snapshot.toObjects(Recipe::class.java)
        } catch (e: Exception) {
            println("Error fetching saved recipes: ${e.message}")
            emptyList() // Return an empty list on failure
        }
    }

    suspend fun isRecipeSaved(recipeId: Int): Boolean {
        val userId = getCurrentUserId() ?: return false
        return try {
            val doc = firestore.collection("users").document(userId)
                .collection("savedRecipes").document(recipeId.toString())
                .get()
                .await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }
}