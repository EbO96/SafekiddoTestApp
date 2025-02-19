package com.safekiddo.testapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val navController by lazy { findNavController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setViews()
        setupUiWithNavigation()
    }

    private fun setViews() {
        setSupportActionBar(activity_main_toolbar)
    }

    private fun findNavController(): NavController {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.activity_main_nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }

    private fun setupUiWithNavigation() {
        val appBarConfig = AppBarConfiguration(navController.graph, null)
        activity_main_toolbar.setupWithNavController(navController, appBarConfig)
        setupActionBarWithNavController(navController)
    }
}