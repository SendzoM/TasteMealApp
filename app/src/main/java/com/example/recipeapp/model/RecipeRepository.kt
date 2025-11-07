package com.example.recipeapp.model

import com.example.recipeapp.network.SpoonacularApiService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RecipeRepository(
    private val apiService: SpoonacularApiService,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val recipeDao: RecipeDao
) {
    private fun getCurrentUserId(): String? = auth.currentUser?.uid

    // --- Network Operations ---
    suspend fun searchRecipes(query: String): List<Recipe> {
        return try {
            val response = apiService.searchRecipes(query = query, number = 20)
            response.results // The response directly gives a List<Recipe>
        } catch (e: Exception) {
            println("Error searching recipes: ${e.message}")
            emptyList()
        }
    }

    suspend fun getRecipes(page: Int, tags: String?): List<Recipe> {
        return try {
            val response = apiService.getRandomRecipes(number = 10, page = page, tags = tags)
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

    // --- Firebase & Room Operations ---
    suspend fun saveRecipe(recipe: Recipe) {
        getCurrentUserId()?.let { userId ->
            // Save to Firebase
            firestore.collection("users").document(userId)
                .collection("savedRecipes").document(recipe.id.toString())
                .set(recipe)
                .await()
            // Also save locally
            recipeDao.insertRecipe(recipe)
        }
    }

    suspend fun removeRecipe(recipeId: Int) {
        getCurrentUserId()?.let { userId ->
            // Remove from Firebase
            firestore.collection("users").document(userId)
                .collection("savedRecipes").document(recipeId.toString())
                .delete()
                .await()
            // Also remove locally
            val recipeToDelete = recipeDao.getRecipeById(recipeId)
            recipeToDelete?.let { recipeDao.deleteRecipe(it) }
        }
    }

    suspend fun getSavedRecipes(): List<Recipe> {
        val userId = getCurrentUserId() ?: return emptyList()
        return try {
            // Fetch from Firestore as the source of truth
            val snapshot = firestore.collection("users").document(userId)
                .collection("savedRecipes")
                .get()
                .await()
            val savedRecipes = snapshot.toObjects(Recipe::class.java)

            // Clear old local cache and insert fresh data
            recipeDao.deleteAllRecipes()
            savedRecipes.forEach { recipeDao.insertRecipe(it) }

            savedRecipes
        } catch (e: Exception) {
            // If network fails, return what we have in the local cache
            println("Error fetching from Firestore, falling back to local cache: ${e.message}")
            recipeDao.getAllRecipes()
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
            // Fallback to check local cache
            recipeDao.getRecipeById(recipeId) != null
        }
    }

}