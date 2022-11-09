package com.maximshuhman.bsuirschedule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
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

    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
        bottomNavigationView = findViewById(R.id.bottom_navigating_view)
        }catch (e:Exception){
            Toast.makeText(this.applicationContext, "Ошибка открытия приложения", Toast.LENGTH_SHORT).show()
            this.recreate()
        }

        val navController = findNavController(R.id.nav_fragment)

        bottomNavigationView.setupWithNavController(navController)
    }
}