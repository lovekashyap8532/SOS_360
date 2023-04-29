package com.example.sos360

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage


class Person3 : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var profileImageView3: ImageView
    private var profileImageUri3: String? = null
    private val REQUEST_IMAGE_PICK = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person3)

        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("SOS360")

        val personnameEditText3 = findViewById<EditText>(R.id.personname3)
        val personemailEditText3 = findViewById<EditText>(R.id.personemail3)
        val personaddressEditText3 = findViewById<EditText>(R.id.personaddress3)
        val personrelationshipEditText3 = findViewById<EditText>(R.id.personrelationship3)
        val personnumberEditText3 = findViewById<EditText>(R.id.personnumber3)
//        val bloodGroupEditText = findViewById<EditText>(R.id.bloodgroup)
//        val diseasesEditText = findViewById<EditText>(R.id.diseases)
//        val lastcheckupEditText = findViewById<EditText>(R.id.lastcheckup)
        val nexttext3 = findViewById<TextView>(R.id.nexttext3)
        val skip1 = findViewById<TextView>(R.id.skip1)
        profileImageView3 = findViewById(R.id.personprofilepic3)

        profileImageView3.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        skip1.setOnClickListener {
            val intent = Intent(this@Person3, Home::class.java)
            startActivity(intent)
            finish()
        }

        nexttext3.setOnClickListener {
            val personname3 = personnameEditText3.text.toString()
            val personemail3 = personemailEditText3.text.toString()
            val personaddress3 = personaddressEditText3.text.toString()
            val personnumber3 = personnumberEditText3.text.toString()
            val personrelationship3 = personrelationshipEditText3.text.toString()

//            val diseases = diseasesEditText.text.toString()
//            val bloodGroup = bloodGroupEditText.text.toString()
//            val lastcheckup = lastcheckupEditText.text.toString()


            if (personname3.isEmpty() || personemail3.isEmpty() || personaddress3.isEmpty() || personnumber3.isEmpty()) {
                // Show an error message that all fields are required
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get a reference to the child node for the current user
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val userRef = usersRef.child(userId)

            // Create a new child node under the user node
            val newChildRef = userRef.push()

            // Create a new User object with the data you want to add
            val user = User(personname3, personemail3, personaddress3, personnumber3, personrelationship3)

            // Add the user data to the new child node
            newChildRef.setValue(user).addOnSuccessListener {
                Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show()
            }



// Upload the profile image
// uploadProfileImage(userId)

// Move to the next activity
            val intent = Intent(this@Person3, Home::class.java)
            startActivity(intent)
            finish()

        }

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                // Set the user data in the UI
                personnameEditText3.setText(user?.personname3)
                personemailEditText3.setText(user?.personemail3)
                personaddressEditText3.setText(user?.personaddress3)
                personnumberEditText3.setText(user?.personnumber3)
                personrelationshipEditText3.setText(user?.personrelationship3)
//                bloodGroupEditText.setText(user?.bloodGroup)
//                diseasesEditText.setText(user?.diseases)
//                lastcheckupEditText.setText(user?.lastcheckup)

                // Load the profile image
                val storageRef = FirebaseStorage.getInstance().reference
                val profileImageRef = storageRef.child("images/$userId/SOS360profile_pic")
                profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(this@Person3)
                        .load(uri)
                        .placeholder(R.drawable.default_profile_image)
                        .error(R.drawable.default_profile_image)
                        .into(profileImageView3)
                }.addOnFailureListener {
                    // Handle the failure to download the profile image
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }



//    private fun uploadProfileImage(userId: String) {
//        if (imageUri == null) {
//            return
//        }
//
//        val storageRef = FirebaseStorage.getInstance().getReference("SOS360profile_pic")
//        val imageRef = storageRef.child("$userId.jpg")
//
//        // Upload the image to Firebase Storage
//        imageRef.putFile(imageUri!!)
//            .addOnSuccessListener {
//                // Image upload successful, get the download URL
//                imageRef.downloadUrl.addOnSuccessListener { uri ->
//                    // Save the download URL in the user's data
//                    usersRef.child(userId).child("profileImageUrl").setValue(uri.toString())
//                }
//            }
//            .addOnFailureListener {
//                // Handle the error
//            }
//    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            uploadProfileImage(imageUri)
        }
    }

    private fun uploadProfileImage(imageUri: Uri?) {
        if (imageUri == null) {
            return
        }

        val storageRef = FirebaseStorage.getInstance().getReference("SOS360profile_pic")
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val imageRef = storageRef.child("$userId.jpg")

        // Upload the image to Firebase Storage
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                // Image upload successful, update the user's profile with the image URL
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val userUpdates = HashMap<String, Any>()
                    userUpdates["profileImageUrl"] = uri.toString()
                    usersRef.child(userId).updateChildren(userUpdates)
                }
                Toast.makeText(this, "Image Uploaded Successfully !", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {
                // Handle image upload failure
            }
    }



    data class User(
        var personname3: String? = null,
        var personemail3: String? = null,
        var personaddress3: String? = null,
        var personnumber3: String? = null,
        var personrelationship3: String? = null,
//        var bloodGroup: String? = null,
        var profileImageUri3: String? = null,
//        var diseases: String? = null,
//        var lastcheckup: String? = null
        // New field to store the profile image URL
    )
}