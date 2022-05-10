package com.assignment.travelbliss.view


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.assignment.travelbliss.MainActivity
import com.assignment.travelbliss.R
import com.assignment.travelbliss.adapter.ListAdapter
import com.assignment.travelbliss.model.Location
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_items.*


class ItemsActivity : AppCompatActivity() {

    private var mStorage:FirebaseStorage? = null
    private var mDatabaseRef: DatabaseReference? = null
    private var mDBListener: ValueEventListener? = null
    private lateinit var mLocation:MutableList<Location>
    private lateinit var listAdapter: ListAdapter
    private lateinit var auth: FirebaseAuth
    private var image: ImageView? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar()?.hide(); // hide the title bar
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        ); //enable full screen
        setContentView(R.layout.activity_items)

        image = findViewById<View>(R.id.posterImage) as ImageView
        image!!.setImageResource(R.drawable.banner1)



        auth = FirebaseAuth.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        // Adapter class is called to inflate the data to the recycler view
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this@ItemsActivity)
        myDataLoaderProgressBar.visibility = View.VISIBLE
        mLocation = ArrayList()
        listAdapter = ListAdapter(this@ItemsActivity, mLocation)
        mRecyclerView.adapter = listAdapter

        // Initialize firebase object
        mStorage = FirebaseStorage.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("location_uploads")
        mDBListener = mDatabaseRef!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ItemsActivity, error.message, Toast.LENGTH_SHORT).show()
                myDataLoaderProgressBar.visibility = View.INVISIBLE
            }
            // When a data changes in the database
            override fun onDataChange(snapshot: DataSnapshot) {
                mLocation.clear()
                for (snap in snapshot.children) {
                    val upload = snap.getValue(Location::class.java)
                    upload!!.key = snap.key
                    mLocation.add(upload)
                }
                listAdapter.notifyDataSetChanged()
                myDataLoaderProgressBar.visibility = View.GONE
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mDatabaseRef!!.removeEventListener(mDBListener!!)
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


