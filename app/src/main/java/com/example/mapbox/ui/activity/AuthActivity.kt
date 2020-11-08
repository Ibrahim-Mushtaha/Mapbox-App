package com.example.mapbox.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mapbox.R
import com.example.mapbox.util.Constant.setUpStatusBar

class AuthActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        setUpStatusBar(this,1)
    }
}