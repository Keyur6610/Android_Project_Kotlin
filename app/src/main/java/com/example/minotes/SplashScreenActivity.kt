package com.example.minotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.airbnb.lottie.LottieAnimationView

class SplashScreenActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        var lottieView:LottieAnimationView = findViewById(R.id.lottieAnimation)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN


        Handler(Looper.getMainLooper()).postDelayed(Runnable {
           startActivity(Intent(this,LoginActivity::class.java))
           finish()
        },2500)

    }
}