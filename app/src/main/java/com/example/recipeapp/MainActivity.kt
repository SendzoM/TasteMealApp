package com.example.recipeapp;

import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewConfiguration
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.recipeapp.R
import com.example.recipeapp.auth.Login
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        //navigationView.setNavigationItemSelectedListener(this)

        // Define top-level destinations (fragments that don't show a "back" arrow)
        // These should match the IDs in your nav_menu.xml
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_about, R.id.nav_settings), // Add all your menu items here
            drawerLayout
        )

        // Connect the Toolbar to the NavController
        // This automatically handles the title and the hamburger/back icon
        setupActionBarWithNavController(navController, appBarConfiguration)
        // Connect the NavigationView to the NavController
        // This automatically handles clicks and navigates to the correct fragment
        navigationView.setupWithNavController(navController)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle logout separately
            if (menuItem.itemId == R.id.nav_logout) {
                // Clear saved category preference
                val sharedPref = getSharedPreferences("RecipeAppPrefs", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    remove("SELECTED_CATEGORY")
                    apply()
                }

                // Sign out from Firebase
                auth.signOut()

                // Redirect to Login screen
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish() // Finish MainActivity to prevent user from coming back
                true
            } else {
                // Let the NavController handle other menu items
                val handled = NavigationUI.onNavDestinationSelected(menuItem, navController)
                // Close the drawer after a menu item is selected
                drawerLayout.closeDrawer(GravityCompat.START)
                handled
            }
        }

        updateNavHeader()
    }

    private fun updateNavHeader() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0) // Get the header view

        // Find all three TextViews in the header
        val headerUserInitials = headerView.findViewById<TextView>(R.id.UserInitials)
        val headerUsername = headerView.findViewById<TextView>(R.id.Username_nav)
        val headerUserEmail =
            headerView.findViewById<TextView>(R.id.UserEmail_nav) // We will add this ID

        // Get the current user from Firebase
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // --- START: FETCH FROM FIRESTORE ---
            val userRef = db.collection("users").document(currentUser.uid)
            userRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val userName = document.getString("username") ?: "Recipe Lover"
                    val userEmail = document.getString("email") ?: "No Email"

                    // Set the username and email from Firestore data
                    headerUsername.text = userName
                    headerUserEmail.text = userEmail

                    // Set the initials
                    if (userName.isNotEmpty()) {
                        headerUserInitials.text = if (userName.length >= 2) {
                            userName.substring(0, 2).uppercase()
                        } else {
                            userName.uppercase()
                        }
                    } else {
                        headerUserInitials.text = "RL" // Fallback initials
                    }
                } else {
                    // Document doesn't exist, use auth data as a fallback
                    populateHeaderWithAuthFallback()
                }
            }.addOnFailureListener {
                // Failed to fetch, use auth data as a fallback
                populateHeaderWithAuthFallback()
            }
        } else {
            // User is not logged in (or data is not available), show default text
            headerUsername.text = "Guest User"
            headerUserEmail.text = "guest@example.com"
            headerUserInitials.text = "GU"
        }
    }

    // Helper function for fallback to prevent code duplication
    private fun populateHeaderWithAuthFallback() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        val headerUserInitials = headerView.findViewById<TextView>(R.id.UserInitials)
        val headerUsername = headerView.findViewById<TextView>(R.id.Username_nav)
        val headerUserEmail = headerView.findViewById<TextView>(R.id.UserEmail_nav)
        val user = auth.currentUser

        val userName = user?.displayName ?: "Recipe Lover"
        val userEmail = user?.email ?: "No Email"

        headerUsername.text = userName
        headerUserEmail.text = userEmail

        if (userName.isNotEmpty()) {
            headerUserInitials.text = if (userName.length >= 2) {
                userName.substring(0, 2).uppercase()
            } else {
                userName.uppercase()
            }
        } else {
            headerUserInitials.text = "RL"
        }
    }

        override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}