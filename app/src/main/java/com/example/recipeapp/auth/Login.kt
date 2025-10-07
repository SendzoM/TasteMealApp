package com.example.recipeapp.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.recipeapp.MainActivity
import com.example.recipeapp.R
import com.example.recipeapp.ui.RecipeDetailsFragment
import com.example.recipeapp.ui.RecipeListFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        //Get references to the EditText views
        val editTextEmail = findViewById<EditText>(R.id.email)
        val editTextPassword = findViewById<EditText>(R.id.password)
        val signInButton = findViewById<Button>(R.id.sign_in)
        val createAccountLink = findViewById<TextView>(R.id.create_account_link)

        signInButton.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            //validate input
            if (email.isEmpty()) {
                editTextEmail.error = "Email cannot be empty"
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextEmail.error = "Please enter a valid email address"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                editTextPassword.error = "Password cannot be empty"
                return@setOnClickListener
            }
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Login failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
        createAccountLink.setOnClickListener {
            startActivity(Intent(this, CreateAccount::class.java))
            finish()
        }
    }
}