package com.example.sos360

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.TextView

@SuppressLint("CustomSplashScreen")
@Suppress("DEPRECATION")
class SplashScreen : androidx.appcompat.app.AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // HERE WE ARE TAKING THE REFERENCE OF OUR IMAGE
        // SO THAT WE CAN PERFORM ANIMATION USING THAT IMAGE

        val simpleText = findViewById<TextView>(R.id.textView1)
        val cg = findViewById<TextView>(R.id.textView2)
        val animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        simpleText.startAnimation(animationFadeOut)
        cg.startAnimation(animationFadeOut)
        AnimationUtils.loadAnimation(this, R.anim.side_slide)
//        backgroundImage.startAnimation(slideAnimation)

        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        Handler().postDelayed({
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}
