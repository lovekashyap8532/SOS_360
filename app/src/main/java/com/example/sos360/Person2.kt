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


class Person2 : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var profileImageView2: ImageView
    private var profileImageUri2: String? = null
    private val REQUEST_IMAGE_PICK = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person2)

        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("SOS360")

        val personnameEditText2 = findViewById<EditText>(R.id.personname2)
        val personemailEditText2 = findViewById<EditText>(R.id.personemail2)
        val personaddressEditText2 = findViewById<EditText>(R.id.personaddress2)
        val personrelationshipEditText2 = findViewById<EditText>(R.id.personrelationship2)
        val personnumberEditText2 = findViewById<EditText>(R.id.personnumber2)
//        val bloodGroupEditText = findViewById<EditText>(R.id.bloodgroup)
//        val diseasesEditText = findViewById<EditText>(R.id.diseases)
//        val lastcheckupEditText = findViewById<EditText>(R.id.lastcheckup)
        val nexttext2 = findViewById<TextView>(R.id.nexttext2)
        profileImageView2 = findViewById(R.id.personprofilepic2)

        profileImageView2.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        nexttext2.setOnClickListener {
            val personname2 = personnameEditText2.text.toString()
            val personemail2 = personemailEditText2.text.toString()
            val personaddress2 = personaddressEditText2.text.toString()
            val personnumber2 = personnumberEditText2.text.toString()
            val personrelationship2 = personrelationshipEditText2.text.toString()

//            val diseases = diseasesEditText.text.toString()
//            val bloodGroup = bloodGroupEditText.text.toString()
//            val lastcheckup = lastcheckupEditText.text.toString()


            if (personname2.isEmpty() || personemail2.isEmpty() || personaddress2.isEmpty() || personnumber2.isEmpty()) {
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
            val user = User(personname2, personemail2, personaddress2, personnumber2, personrelationship2)

            // Add the user data to the new child node
            newChildRef.setValue(user).addOnSuccessListener {
                Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show()
            }



// Upload the profile image
// uploadProfileImage(userId)

// Move to the next activity
            val intent = Intent(this@Person2, Person3::class.java)
            startActivity(intent)
            finish()

        }

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                // Set the user data in the UI
                personnameEditText2.setText(user?.personname2)
                personemailEditText2.setText(user?.personemail2)
                personaddressEditText2.setText(user?.personaddress2)
                personnumberEditText2.setText(user?.personnumber2)
                personrelationshipEditText2.setText(user?.personrelationship2)
//                bloodGroupEditText.setText(user?.bloodGroup)
//                diseasesEditText.setText(user?.diseases)
//                lastcheckupEditText.setText(user?.lastcheckup)

                // Load the profile image
                val storageRef = FirebaseStorage.getInstance().reference
                val profileImageRef = storageRef.child("images/$userId/SOS360profile_pic")
                profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(this@Person2)
                        .load(uri)
                        .placeholder(R.drawable.default_profile_image)
                        .error(R.drawable.default_profile_image)
                        .into(profileImageView2)
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
        var personname2: String? = null,
        var personemail2: String? = null,
        var personaddress2: String? = null,
        var personnumber2: String? = null,
        var personrelationship2: String? = null,
//        var bloodGroup: String? = null,
        var profileImageUri2: String? = null,
//        var diseases: String? = null,
//        var lastcheckup: String? = null
        // New field to store the profile image URL
    )
}