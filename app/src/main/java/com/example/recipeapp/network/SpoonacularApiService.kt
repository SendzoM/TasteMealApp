package com.example.recipeapp.network

import com.example.recipeapp.model.RecipeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SpoonacularApiService {
    @GET("recipes/random")
    suspend fun getRandomRecipes(
        @Query("number") number: Int = 10
    ): RecipeResponse



    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("query") query: String,
        @Query("number") number: Int = 10
    ): RecipeResponse

}