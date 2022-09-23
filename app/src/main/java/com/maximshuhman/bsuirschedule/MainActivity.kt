package com.maximshuhman.bsuirschedule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration

import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.runBlocking
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class MainActivity : AppCompatActivity() {

    //Naviga



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigating_view)
        val navController = findNavController(R.id.nav_fragment)


        // as soon as the application opens the first fragment should
        // be shown to the user in this case it is algorithm fragment


        val req = Requests()

        //req.run()

        val example = GetExample()
        val response = example.run("https://raw.github.com/square/okhttp/master/README.md")
        println(response)
     /*   runBlocking {

            req.getGroupSchedule("https://iis.bsuir.by/api/v1/schedule?studentGroup=220603")

        }*/
        bottomNavigationView.setupWithNavController(navController)

   // loadRandomFact()
    }




}