package com.example.recipeapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val sourceUrl: String?,
    val summary: String?
) : Parcelable
