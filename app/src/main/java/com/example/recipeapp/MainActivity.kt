package com.example.recipeapp;

import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
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
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.recipeapp.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

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
        val user = auth.currentUser

        if (user != null) {
            // User is logged in, let's get their data
            val userName = user.displayName ?: "Recipe Lover" // Fallback name
            val userEmail = user.email ?: "No Email" // Fallback email

            // Set the username and email
            headerUsername.text = userName
            headerUserEmail.text = userEmail

            // Set the initials (first two letters, uppercase)
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
            // User is not logged in (or data is not available), show default text
            headerUsername.text = "Guest User"
            headerUserEmail.text = "guest@example.com"
            headerUserInitials.text = "GU"
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