package com.assignment.travelbliss.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.assignment.travelbliss.R
import com.assignment.travelbliss.uitel.loadImage
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar()?.hide(); // hide the title bar
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        ); //enable full screen
        setContentView(R.layout.activity_details)

        val intss = intent
        var nameT = intss.getStringExtra("NAMET")
        var desT = intss.getStringExtra("DESCRIT")
        var imgT = intss.getStringExtra("IMGURI")
        var ratT = intss.getStringExtra("RATING")
        var ltln = intss.getStringExtra("LOCAT")

        nameDetailTextView.text = nameT
        descriptionDetailTextView.text = desT
        ratingDetailTextView.text = ratT
        teacherDetailImageView.loadImage(imgT)
        AddressDetailTextView.text = ltln


    }
}