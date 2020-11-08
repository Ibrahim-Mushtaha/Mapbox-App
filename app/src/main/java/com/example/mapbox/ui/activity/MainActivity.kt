package com.example.mapbox.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.example.mapbox.R
import com.example.mapbox.util.Constant


class MainActivity : AppCompatActivity(){


    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var navController: NavController
    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Constant.setUpStatusBar(this, 0)


        navController = findNavController(R.id.fragment)
        drawerLayout = findViewById(R.id.drawer_layout)
        //   appBarConfiguration = AppBarConfiguration(navController.graph,drawerLayout)
        appBarConfiguration = AppBarConfiguration(
            setOf(
              R.id.map_Fragment
            ),drawerLayout
        )



       // navigation_view.setupWithNavController(navController)
      //  setupActionBarWithNavController(navController, appBarConfiguration)

      /*  findViewById<Toolbar>(R.id.main2_toolbar)
            .setupWithNavController(navController, appBarConfiguration)*/


    }


}
