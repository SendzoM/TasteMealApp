package com.example.recipeapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val sourceUrl: String?,
    val summary: String?,
    val extendedIngredients: List<Ingredient>?,
    val analyzedInstructions: List<Instruction>?
) : Parcelable

@Parcelize
data class Ingredient(
    val original: String // We just need the original string for the ingredient
) : Parcelable

@Parcelize
data class Instruction(
    val name: String,
    val steps: List<Step>
) : Parcelable

@Parcelize
data class Step(
    val number: Int,
    val step: String
) : Parcelable
