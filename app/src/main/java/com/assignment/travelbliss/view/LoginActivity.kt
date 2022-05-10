package com.assignment.travelbliss.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.assignment.travelbliss.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var  auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        title="Login"

        // Creating a new Firebase object
        auth= FirebaseAuth.getInstance()
    }

    // Function for checking email address and password
    fun login(view: View){
        val email=editTextEmailAddress.text.toString()
        val password=editTextPassword.text.toString()
        // Checking if Email & Password is Valid
        if(email.length > 0 && password.length > 0) {
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                // If sign in is successful, navigate to ItemsActivity
                if(task.isSuccessful){
                    val intent= Intent(this, ItemsActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
        } else {
            // If Email or Password is empty
            Toast.makeText(applicationContext,"Please Enter All Fields", Toast.LENGTH_LONG).show()
        }
    }

    fun goToRegister(view: View){
        val intent= Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    fun goToMain(view: View) {
        val intent= Intent(this, ItemsActivity::class.java)
        startActivity(intent)
    }
}