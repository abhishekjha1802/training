package com.example.myfirstapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myfirstapp.fragments.AddFragment
import com.example.myfirstapp.fragments.HomeFragment
import com.example.myfirstapp.fragments.UserlistFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var drawerLayout:DrawerLayout
    lateinit var navigationView:NavigationView
    lateinit var navController:NavController
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        toolbar=findViewById(R.id.myToolbar)
        setSupportActionBar(toolbar)

        drawerLayout=findViewById(R.id.drawer)
        navigationView=findViewById(R.id.navigationView)
        bottomNavigationView=findViewById(R.id.bottom_navigation)

        navController=findNavController(R.id.fragmentContainerView)
        appBarConfiguration= AppBarConfiguration(setOf(R.id.id_home_fragment,R.id.id_userlist_fragment,R.id.id_add_fragment),drawerLayout)
        setupActionBarWithNavController(navController,drawerLayout)

        println(R.id.id_home_fragment)
        println(R.id.fragmentContainerView)
        navigationView.setupWithNavController(navController)
        bottomNavigationView.setupWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        var navController=findNavController(R.id.fragmentContainerView)
        return navController.navigateUp(appBarConfiguration) ||super.onSupportNavigateUp()
    }

    var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (navController.currentDestination!!.id == navController.graph.startDestination) {
            if (doubleBackToExitPressedOnce)
                super.onBackPressed()
            else
                onHandlerStart()
        }else
            super.onBackPressed()
    }
    private fun onHandlerStart() {
        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

}
