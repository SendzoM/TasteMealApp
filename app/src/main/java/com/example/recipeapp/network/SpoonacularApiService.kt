package com.example.recipeapp.network

import com.example.recipeapp.model.Recipe
import com.example.recipeapp.model.RecipeResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpoonacularApiService {
    @GET("recipes/random")
    suspend fun getRandomRecipes(
        @Query("number") number: Int = 10,
        @Query("page") page: Int,
        @Query("tags") tags: String?
    ): RecipeResponse

    @GET("recipes/{id}/information")
    suspend fun getRecipeInformation(
        @Path("id") id: Int
    ): Recipe

    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("query") query: String,
        @Query("number") number: Int = 10
    ): RecipeResponse

}