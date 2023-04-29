package com.example.sos360

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage


class Myinfo : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var profileImageView: ImageView
    private var imageUri: Uri? = null
    private val REQUEST_IMAGE_PICK = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myinfo)

        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("SOS360")

        val nameEditText = findViewById<EditText>(R.id.name)
        val fathernameEditText = findViewById<EditText>(R.id.fathername)
        val mothernameEditText = findViewById<EditText>(R.id.mothername)
        val alternativenumberEditText = findViewById<EditText>(R.id.alternativenumber)
        val addressEditText = findViewById<EditText>(R.id.address)
        val bloodgroupEditText = findViewById<EditText>(R.id.bloodgroup)
        val diseasesEditText = findViewById<EditText>(R.id.diseases)
        val lastcheckupEditText = findViewById<EditText>(R.id.lastcheckup)
        val nextButton = findViewById<Button>(R.id.nextbutton)
        profileImageView = findViewById(R.id.userprofilepic)

        profileImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        nextButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val fathername = fathernameEditText.text.toString()
            val mothername = mothernameEditText.text.toString()
            val alternativenumber = alternativenumberEditText.text.toString()
            val address = addressEditText.text.toString()
            val diseases = diseasesEditText.text.toString()
            val bloodgroup = bloodgroupEditText.text.toString()
            val lastcheckup = lastcheckupEditText.text.toString()


            if (name.isEmpty() || fathername.isEmpty() || mothername.isEmpty() || alternativenumber.isEmpty() || address.isEmpty()) {
                // Show an error message that all fields are required
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(name, fathername, mothername, alternativenumber, address, bloodgroup, diseases, lastcheckup )
            val userId = FirebaseAuth.getInstance().currentUser!!.uid

            usersRef.child(userId).setValue(user)

            // Upload the profile image
//            uploadProfileImage(userId)

            // Move to the next activity
            val intent = Intent(this@Myinfo, Person1::class.java)
            startActivity(intent)
            finish()
        }

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                // Set the user data in the UI
                nameEditText.setText(user?.name)
                fathernameEditText.setText(user?.fathername)
                mothernameEditText.setText(user?.mothername)
                alternativenumberEditText.setText(user?.alternativenumber)
                addressEditText.setText(user?.address)
                bloodgroupEditText.setText(user?.bloodgroup)
                diseasesEditText.setText(user?.diseases)
                lastcheckupEditText.setText(user?.lastcheckup)

                // Load the profile image
                val storageRef = FirebaseStorage.getInstance().reference
                val profileImageRef = storageRef.child("images/$userId/SOS360profile_pic")
                profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(this@Myinfo)
                        .load(uri)
                        .placeholder(R.drawable.default_profile_image)
                        .error(R.drawable.default_profile_image)
                        .into(profileImageView)
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
        var name: String? = null,
        var fathername: String? = null,
        var mothername: String? = null,
        var alternativenumber: String? = null,
        var address: String? = null,
        var bloodgroup: String? = null,
        var diseases: String? = null,
        var lastcheckup: String? = null,
        var profileImageUri: String? = null

        // New field to store the profile image URL
    )
}