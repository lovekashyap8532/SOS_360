package com.example.sos360

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class Profile : AppCompatActivity() {

    class User(
//        var useremail:String? = "",
        var userprofilepic: String? = "",
        var name: String? = "",
        var fathername: String? = "",
        var mothername: String? = "",
        var alternativenumber: String? = "",
        var address: String? = "",
        var bloodgroup: String? = "",
        var diseases: String? = "",
        var lastcheckup: String? = ""
    )

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        databaseReference = FirebaseDatabase.getInstance().getReference("SOS360/$userId")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)

                val userProfilePicImageView = findViewById<ImageView>(R.id.userprofilepic)
                if (!user?.userprofilepic.isNullOrEmpty()) {
                    Picasso.get().load(user?.userprofilepic).into(userProfilePicImageView)
                } else {
                    // handle null or empty user profile picture URL
                    val defaultImageUrl = "https://www.example.com/default_image.png"
                    Picasso.get().load(defaultImageUrl).into(userProfilePicImageView)
                }


//                val useremailTextView = findViewById<TextView>(R.id.useremail)
//                useremailTextView.text = user?.useremail

                val nameTextView = findViewById<TextView>(R.id.name)
                nameTextView.text = user?.name

                val fatherNameTextView = findViewById<TextView>(R.id.fathername)
                fatherNameTextView.text = user?.fathername

                val motherNameTextView = findViewById<TextView>(R.id.mothername)
                motherNameTextView.text = user?.mothername

                val alternativeNumberTextView = findViewById<TextView>(R.id.alternativenumber)
                alternativeNumberTextView.text = user?.alternativenumber

                val addressTextView = findViewById<TextView>(R.id.address)
                addressTextView.text = user?.address

                val bloodGroupTextView = findViewById<TextView>(R.id.bloodgroup)
                bloodGroupTextView.text = user?.bloodgroup

                val diseasesTextView = findViewById<TextView>(R.id.diseases)
                diseasesTextView.text = user?.diseases

                val lastCheckupTextView = findViewById<TextView>(R.id.lastcheckup)
                lastCheckupTextView.text = user?.lastcheckup
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                Log.w("Profile", "loadPost:onCancelled", databaseError.toException())
            }
        })

        val editbutton = findViewById<TextView>(R.id.logout)
        editbutton.setOnClickListener {
            val intent = Intent(this,Myinfo::class.java)
            startActivity(intent)
        }
    }
}

