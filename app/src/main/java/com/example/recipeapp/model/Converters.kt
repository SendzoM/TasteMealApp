package com.example.recipeapp.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    // Converter for Ingredient list
    @TypeConverter
    fun fromIngredientList(ingredients: List<Ingredient>?): String? {
        return ingredients?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toIngredientList(ingredientsString: String?): List<Ingredient>? {
        return ingredientsString?.let {
            val listType = object : TypeToken<List<Ingredient>>() {}.type
            gson.fromJson(it, listType)
        }
    }

    // Converter for Instruction list
    @TypeConverter
    fun fromInstructionList(instructions: List<Instruction>?): String? {
        return instructions?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toInstructionList(instructionsString: String?): List<Instruction>? {
        return instructionsString?.let {
            val listType = object : TypeToken<List<Instruction>>() {}.type
            gson.fromJson(it, listType)
        }
    }
}