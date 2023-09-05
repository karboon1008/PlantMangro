package com.example.recycleviewwithclicklistener.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.recycleviewwithclicklistener.MainActivity
import com.example.recycleviewwithclicklistener.R
import com.example.recycleviewwithclicklistener.SignUp.signUp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LogIn : AppCompatActivity() {

    companion object{
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var loginStatusManager: LoginStatusManager
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        // Instantiate
        auth = FirebaseAuth.getInstance()
        loginStatusManager = LoginStatusManager(this)

        val logInEmail: TextInputEditText = findViewById(R.id.loginEmail)
        val logInPassword: TextInputEditText = findViewById(R.id.loginPassword)
        val logInPasswordLayout: TextInputLayout = findViewById(R.id.layoutLoginPassword)
        val logInBtn: Button = findViewById(R.id.loginBtn)
        val progressBar: ProgressBar = findViewById(R.id.logInProgressBar)
        val changeSignUp : TextView = findViewById(R.id.ChangeSignInBtn)
        val googleBtn : LinearLayout = findViewById(R.id.googleBtn)

        googleBtn.setOnClickListener{
            googleSignIn()
        }

        changeSignUp.setOnClickListener{
            val intent = Intent(this, signUp::class.java)
            startActivity(intent)
        }

        logInBtn.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            logInPasswordLayout.isPasswordVisibilityToggleEnabled = true

            val email = logInEmail.text.toString()
            val password = logInPassword.text.toString()

            if(email.isEmpty() || password.isEmpty()){
                if(email.isEmpty()){
                    logInEmail.error = "Enter your email address"
                }
                if(password.isEmpty()){
                    logInPassword.error = "Enter your password"
                    logInPasswordLayout.isPasswordVisibilityToggleEnabled = false
                }
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Enter valid details", Toast.LENGTH_SHORT).show()
            }else if(!email.matches(emailPattern.toRegex())){
                progressBar.visibility = View.GONE
                logInEmail.error = "Enter valid email address"
                Toast.makeText(this, "Enter valid email address", Toast.LENGTH_SHORT).show()
            }else if(password.length < 6){
                logInPasswordLayout.isPasswordVisibilityToggleEnabled = false
                progressBar.visibility = View.GONE
                logInPassword.error = "Enter password more than 6 characters"
                Toast.makeText(this, "Enter password more than 6 characters", Toast.LENGTH_SHORT).show()
            }else{
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                    if(it.isSuccessful){
                        handleLoginSuccess(email, password)
                    }else{
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun redirectToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Save login details and redirect to MainActivity
    private fun handleLoginSuccess(email: String, password: String) {
        loginStatusManager.saveLoginDetails(email, password)
        redirectToMainActivity()
    }

    // google sign in
    private fun googleSignIn(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)
                firebaseAuthGoogle(account.idToken!!)
            }catch (e:ApiException){
                Toast.makeText(this, "Google sign in failed, try again: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener (this){ task ->
                if(task.isSuccessful){
                    val user = auth.currentUser
                    Toast.makeText(this, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this, "Authentication failed",Toast.LENGTH_SHORT).show()
                }
            }
    }
}