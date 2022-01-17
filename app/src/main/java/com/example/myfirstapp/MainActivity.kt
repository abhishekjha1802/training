package com.example.myfirstapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
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


        navigationView.setupWithNavController(navController)
        bottomNavigationView.setupWithNavController(navController)



    }

    override fun onSupportNavigateUp(): Boolean {
        var navController=findNavController(R.id.fragmentContainerView)
        return navController.navigateUp(appBarConfiguration) ||super.onSupportNavigateUp()
    }


}
