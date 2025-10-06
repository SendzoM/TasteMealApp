package com.example.recipeapp;
import androidx.appcompat.app.AppCompatActivity

import android.app.Activity;
import android.os.Bundle
import com.example.recipeapp.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}