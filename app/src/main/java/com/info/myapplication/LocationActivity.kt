package com.info.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class LocationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        var fragmentmanager: FragmentManager = supportFragmentManager
        var ft: FragmentTransaction = fragmentmanager.beginTransaction()
        ft.add(R.id.content, LocationFragment(), "test")
        ft.commit()
    }
}
