package com.example.recycleviewwithclicklistener.SignUp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.recycleviewwithclicklistener.Login.LogIn
import com.example.recycleviewwithclicklistener.R
import com.example.recycleviewwithclicklistener.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class signUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var signUpDatabase: FirebaseDatabase
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectImageBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        binding.ChangeLogInBtn.setOnClickListener {
            Log.d("signUpActivity", "Try to show login activity")

            // launch login acitivty
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
        }

        binding.signUpBtn.setOnClickListener {
            performSignUp()
        }
    }

    var selectedImageUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectTV: TextView = findViewById(R.id.selectTV)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            // check what the selected image was
            selectedImageUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)

            binding.selectImageView.setImageBitmap(bitmap)

            //val bitmapDrawable = BitmapDrawable(this.resources,bitmap)
            //selectImageBtn.background = bitmapDrawable
            // cancel the select textview
            selectTV.text = ""
        }
    }


    private fun performSignUp(){
        val name = binding.signUpUsername.text.toString()
        val phone = binding.signUpPhone.text.toString()
        val email = binding.signUpEmail.text.toString()
        val password = binding.signUpPassword.text.toString()
        val confirmPassword = binding.signUpConfirmPassword.text.toString()

        // make the input into string
        Log.d("signUpActivity", "Name is: " + name)
        Log.d("signUpActivity", "Phone is: " + phone)
        Log.d("signUpActivity", "Email is: " + email)
        Log.d("signUpActivity", "Password is: " + password)
        Log.d("signUpActivity", "confirmPassword is: " + confirmPassword)

        auth = FirebaseAuth.getInstance()
        signUpDatabase = FirebaseDatabase.getInstance()

        // after click sign up button, progress bar is visible
        binding.signUpProgressBar.visibility = View.VISIBLE
        // password visibility
        binding.layoutSignUpPassword.isPasswordVisibilityToggleEnabled = true
        binding.layoutSignUpConfirmPassword.isPasswordVisibilityToggleEnabled = true

        // check if any is empty and valid
        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            if(name.isEmpty()){
                binding.signUpUsername.error = "Enter your name"
            }
            if(phone.isEmpty()){
                binding.signUpPhone.error = "Enter your phone"
            }
            if(email.isEmpty()){
                binding.signUpEmail.error = "Enter your email"
            }
            if(password.isEmpty()){
                binding.signUpPassword.error = "Enter your password"
            }
            if(confirmPassword.isEmpty()){
                binding.signUpConfirmPassword.error = "Enter your confirm password"
            }
            Toast.makeText(this, "Please fill in ALL details", Toast.LENGTH_SHORT).show()
            binding.signUpProgressBar.visibility = View.GONE

        }else if (!email.matches(emailPattern.toRegex())){
            binding.signUpProgressBar.visibility = View.GONE
            binding.signUpEmail.error = "Enter valid email address"
            Toast.makeText(this, "Enter valid email address", Toast.LENGTH_SHORT).show()
        }else if(phone.length==10){
            binding.signUpProgressBar.visibility = View.GONE
            binding.signUpPhone.error = "Enter valid phone number"
            Toast.makeText(this, "Enter valid phone number", Toast.LENGTH_SHORT).show()
        }else if(password.length < 6){
            binding.layoutSignUpPassword.isPasswordVisibilityToggleEnabled = false
            binding.signUpProgressBar.visibility = View.GONE
            binding.signUpPassword.error = "Enter password more than 6 characters"
            Toast.makeText(this, "Enter password more than 6 characters", Toast.LENGTH_SHORT).show()
        }else if(confirmPassword!=password){
            binding.layoutSignUpConfirmPassword.isPasswordVisibilityToggleEnabled = false
            binding.signUpProgressBar.visibility = View.GONE
            binding.signUpConfirmPassword.error = "Password not matched, try again"
            Toast.makeText(this, "Password not matched, try again", Toast.LENGTH_SHORT).show()
        }else{
            // insert email and password into auth
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                if(!it.isSuccessful)return@addOnCompleteListener

                // else if successful
                Log.d("signUpActivity", "Successfully create user with uid: ${it.result.user!!.uid}")

                uploadImageToFirebaseStorage()
            }
        }
    }

    private fun uploadImageToFirebaseStorage() {
        // if selectedImageUri is null, the rest of code wont be executed
        if (selectedImageUri == null) return
        // UUID = Unique ID
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                Log.d("signUpActivity", "Successfully uploaded image: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterActivity", "File location: $it")
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("signUpActivity", "Something wrong with upload image to database")
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUri: String){
        // insert data into realtime database under id child
        val databaseRef = signUpDatabase.reference.child("users").child(auth.currentUser!!.uid)
        val users : Users = Users(binding.signUpUsername.text.toString(), binding.signUpPhone.text.toString(), binding.signUpEmail.text.toString(), auth.currentUser!!.uid, profileImageUri)

        // if complete, navigate to login page
        databaseRef.setValue(users).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = Intent(this, LogIn::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}