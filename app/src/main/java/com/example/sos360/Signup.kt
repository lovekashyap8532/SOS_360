package com.example.sos360

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sos360.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Signup : AppCompatActivity() {
    data class User(val user : String? = null, val email : String? = null , val password : String? = null ,val phone : String? = null)

    private lateinit var auth: FirebaseAuth
    private lateinit var user: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmpassword: EditText
    private lateinit var phone: EditText
    private lateinit var signupbutton: Button
    private lateinit var binding: ActivitySignupBinding
    private lateinit var database : DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_signup)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        binding.signupbutton.setOnClickListener {
            val nameInput = binding.user.text.toString()
            val emailInput = binding.email.text.toString()
            val passwordInput = binding.password.text.toString()
            val phoneInput = binding.phone.text.toString()
//            val confirmPasswordInput = confirmpassword.text.toString()
            if (nameInput.isEmpty() || emailInput.isEmpty() || passwordInput.isEmpty() || phoneInput.isEmpty()) {

//                .addOnFailureListener {
//                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
//
//                }
            }else {
                // Create user in Firebase Auth
                auth.createUserWithEmailAndPassword(emailInput, passwordInput)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Redirect to ProfileActivity
                            database = FirebaseDatabase.getInstance().getReference("User")
                            val User = User(nameInput, passwordInput, emailInput, phoneInput)
                            database.child(nameInput).setValue(User).addOnSuccessListener {
                                binding.user.text.clear()
                                binding.email.text.clear()
                                binding.password.text.clear()
//                    binding.confirmpassword.text.clear()
                                binding.phone.text.clear()
                                Toast.makeText(this, "Sign up Successfully ", Toast.LENGTH_SHORT).show()
                            }
                            val intent = Intent(this, Myinfo::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // If sign up fails, display a message to the user.
                            Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }

            val storage = FirebaseStorage.getInstance()
            var storageRef: StorageReference = storage.reference

            // Bind the name, email, password, and confirm password EditText fields
            user = findViewById(R.id.user)
            email = findViewById(R.id.email)
            password = findViewById(R.id.password)
            confirmpassword = findViewById(R.id.confirmpassword)

            // Bind the signup button
            signupbutton = findViewById(R.id.signupbutton)

            // Set onClickListener for signup button
//            signupbutton.setOnClickListener {
//                // Get user input from EditText fields
//                val nameInput = user.text.toString()
//                val emailInput = email.text.toString()
//                val passwordInput = password.text.toString()
//                val confirmPasswordInput = confirmpassword.text.toString()
//                val phoneInput = phone.text.toString()
//
//
//                // Validate user input
//                if (nameInput.isEmpty() || emailInput.isEmpty() || passwordInput.isEmpty() || confirmPasswordInput.isEmpty()) {
//                    // Display an error message to the user indicating that all fields are required
//                    Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
//                } else if (passwordInput != confirmPasswordInput) {
//                    // Display an error message to the user indicating that the passwords do not match
//                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
//                } else {
//                    // Create user in Firebase Auth
//                    auth.createUserWithEmailAndPassword(emailInput, passwordInput)
//                        .addOnCompleteListener(this) { task ->
//                            if (task.isSuccessful) {
//                                // Redirect to ProfileActivity
//                                val intent = Intent(this, Myinfo::class.java)
//                                startActivity(intent)
//                                finish()
//                            } else {
//                                // If sign up fails, display a message to the user.
//                                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT)
//                                    .show()
//                            }
//                        }
//                }
//            }

        }

    }
}
