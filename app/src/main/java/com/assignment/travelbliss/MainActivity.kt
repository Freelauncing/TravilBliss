package com.assignment.travelbliss

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.assignment.travelbliss.view.ItemsActivity
import com.assignment.travelbliss.view.LoginActivity

import com.assignment.travelbliss.view.UploadActivity
import com.google.firebase.auth.FirebaseAuth


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var btnViewAll = findViewById(R.id.btnViewAll) as Button
        btnViewAll.setOnClickListener {
            startActivity(Intent(this, ItemsActivity::class.java))
        }


        var btnLogin = findViewById(R.id.btnLogin) as Button
        var buttonSignOut = findViewById(R.id.buttonSignOut) as Button
        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        auth = FirebaseAuth.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        if(user==null){
            btnLogin.setVisibility(View.VISIBLE); //SHOW the button
            buttonSignOut.setVisibility(View.GONE); //SHOW the button

        }
        else {
            btnLogin.setVisibility(View.GONE); //SHOW the button
            buttonSignOut.setVisibility(View.VISIBLE); //SHOW the button
        }


    }

    fun signOut(view: View) {
        auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun goProfile(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    fun goHome(view: View) {
        val intent = Intent(this, ItemsActivity::class.java)
        startActivity(intent)
    }
    fun AddLoc(view: View) {
        val intent = Intent(this, UploadActivity::class.java)
        startActivity(intent)
    }


}