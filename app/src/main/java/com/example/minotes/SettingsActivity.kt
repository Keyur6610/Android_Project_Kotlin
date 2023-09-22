package com.example.minotes

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    lateinit var darkModeSwitch: Switch
    lateinit var txtSettings:TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        darkModeSwitch = findViewById(R.id.darkModeSwitch)
        txtSettings = findViewById(R.id.txtSettings)

        darkModeSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if (darkModeSwitch.isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            txtSettings.setTextColor(resources.getColor(R.color.white))
            darkModeSwitch.setTextColor(resources.getColor(R.color.white))
        }


    }
}