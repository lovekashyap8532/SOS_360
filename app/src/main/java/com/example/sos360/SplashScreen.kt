package com.example.sos360

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
@Suppress("DEPRECATION")
class SplashScreen : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val imageView = findViewById<ImageView>(R.id.imageView2)

// Load the animation from the anim folder
        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_out)

// Set the animation to the ImageView
        imageView.startAnimation(animation)

// Set a listener to start the next activity when the animation finishes
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                // Start the next activity
                val intent = Intent(this@SplashScreen, Home::class.java)
                startActivity(intent)

                // Finish the current activity
                finish()
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })

    }
}
