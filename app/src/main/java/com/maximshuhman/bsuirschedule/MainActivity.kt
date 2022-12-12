package com.maximshuhman.bsuirschedule

import android.database.Cursor
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.maximshuhman.bsuirschedule.DataBase.DBContract
import com.maximshuhman.bsuirschedule.DataBase.DbHelper

class MainActivity : AppCompatActivity() {

    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            bottomNavigationView = findViewById(R.id.bottom_navigating_view)
        } catch (e: Exception) {
            Toast.makeText(
                this.applicationContext,
                "Ошибка открытия приложения",
                Toast.LENGTH_SHORT
            ).show()
            this.recreate()
        }

        val navController = findNavController(R.id.nav_fragment)

        bottomNavigationView.setupWithNavController(navController)

        val dbHelper = DbHelper(this.applicationContext)
        val db = dbHelper.writableDatabase
        val c: Cursor = db.rawQuery(
            "SELECT COUNT(*) as cnt FROM ${DBContract.Favorites.TABLE_NAME}",
            null
        )
        c.moveToFirst()

        if(c.getInt(0) != 0)
            bottomNavigationView.setSelectedItemId(R.id.favoritesFragment);

    }
}