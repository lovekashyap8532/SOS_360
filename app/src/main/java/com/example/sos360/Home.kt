package com.example.sos360

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class Home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

//        val intent = Intent(this,Background::class.java)
//        startActivity(intent)
        val intent = Intent(this, Background::class.java)
        startService(intent)


        val pimage = findViewById<ImageView>(R.id.homeprofile)
        pimage.setOnClickListener {
            val intent = Intent(this,Profile::class.java)
            startActivity(intent)
        }
        val homecont = findViewById<LinearLayout>(R.id.contacts)
        homecont.setOnClickListener {
            val intent = Intent(this,Contacts::class.java)
            startActivity(intent)
        }
        val hometips = findViewById<LinearLayout>(R.id.tips)
        hometips.setOnClickListener {
            val intent = Intent(this,Tips::class.java)
            startActivity(intent)
        }
        val homehist = findViewById<LinearLayout>(R.id.chistory)
        homehist.setOnClickListener {
            val intent = Intent(this,History::class.java)
            startActivity(intent)
        }
        val homefeed = findViewById<LinearLayout>(R.id.homefeedback)
        homefeed.setOnClickListener {
            val intent = Intent(this,Feedback::class.java)
            startActivity(intent)
        }
        val homesett = findViewById<LinearLayout>(R.id.settings)
        homesett.setOnClickListener {
            val intent = Intent(this,Settings::class.java)
            startActivity(intent)
        }

    }
}

