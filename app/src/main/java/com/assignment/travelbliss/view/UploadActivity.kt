package com.assignment.travelbliss.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.assignment.travelbliss.MainActivity
import com.assignment.travelbliss.PermissionUtils
import com.assignment.travelbliss.R
import com.assignment.travelbliss.model.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import kotlinx.android.synthetic.main.activity_upload.*


class UploadActivity : AppCompatActivity() {

    var context: Context? = null
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
    }

    private var mImageUri: Uri? = null
    private var mStorageRef: StorageReference? = null
    private var mDatabaseRef: DatabaseReference? = null
    private var mUploadTask: StorageTask<*>? = null
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var auth: FirebaseAuth

    private var locationData = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar()?.hide(); // hide the title bar
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        ); //enable full screen
        setContentView(R.layout.activity_upload)


        /**set data*/

        mStorageRef = FirebaseStorage.getInstance().getReference("location_uploads")
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("location_uploads")

        button_choose_image.setOnClickListener { openFileChoose() }
        upLoadBtn.setOnClickListener {
            if (mUploadTask != null && mUploadTask!!.isInProgress) {
                Toast.makeText(
                    this@UploadActivity,
                    "An Upload is Still in Progress",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                uploadFile()
            }
        }


    }

    ////////////////////////////////////////////////////////////////////////////


    private fun setUpLocationListener() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // for getting the current location update after every 2 seconds with high accuracy
        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return
        }
        Looper.myLooper()?.let {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        for (location in locationResult.locations) {
                            latTextView.text = location.latitude.toString()
                            lngTextView.text = location.longitude.toString()
                            locationData = "latitude : " + latTextView.text as String + " and latitude : "+ lngTextView.text as String
                        }
                        // Few more things we can do here:
                        // For example: Update the location of user on server
                    }
                },
                it
            )
        }
    }

    // On start of the application, asks for location permission if not granted
    override fun onStart() {
        super.onStart()
        when {
            PermissionUtils.isAccessFineLocationGranted(this) -> {
                when {
                    PermissionUtils.isLocationEnabled(this) -> {
                        setUpLocationListener()
                    }
                    else -> {
                        PermissionUtils.showGPSNotEnabledDialog(this)
                    }
                }
            }
            else -> {
                PermissionUtils.requestAccessFineLocationPermission(
                    this,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    // When the permission is granted / denied
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when {
                        PermissionUtils.isLocationEnabled(this) -> {
                            setUpLocationListener()
                        }
                        else -> {
                            PermissionUtils.showGPSNotEnabledDialog(this)
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.location_permission_not_granted),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////

    private fun openFileChoose() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            mImageUri = data.data
            chooseImageView.setImageURI(mImageUri)
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val cR = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    private fun uploadFile() {
        auth = FirebaseAuth.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(
                this@UploadActivity,
                "Please Log in to Upload Data",
                Toast.LENGTH_LONG
            )
                .show()

        } else {
            if (mImageUri != null) {
                val fileReference = mStorageRef!!.child(
                    System.currentTimeMillis()
                        .toString() + "." + getFileExtension(mImageUri!!)
                )
                progressBar.visibility = View.VISIBLE
                progressBar.isIndeterminate = true
                mUploadTask = fileReference.putFile(mImageUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        if (taskSnapshot.metadata != null) {
                            if (taskSnapshot.metadata!!.reference != null) {
                                val result: Task<Uri> = taskSnapshot.storage.downloadUrl
                                result.addOnSuccessListener(OnSuccessListener<Uri> { uri ->
                                    var downloadUrl: String = uri.toString()
                                    val handler = Handler()
                                    handler.postDelayed({
                                        progressBar.visibility = View.VISIBLE
                                        progressBar.isIndeterminate = false
                                        progressBar.progress = 0
                                    }, 500)
                                    Toast.makeText(
                                        this@UploadActivity,
                                        "Location data Upload successful",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                    val upload = Location(
                                        name = nameEditText!!.text.toString().trim { it <= ' ' },
                                        imageUrl = downloadUrl,
                                        description = descriptionEditText!!.text.toString().trim { it <= ' ' },
                                        rating = ratingEditText!!.text.toString().trim { it <= ' ' },
                                        locationLtLn = locationData
                                    )
                                    val uploadId = mDatabaseRef!!.push().key
                                    mDatabaseRef!!.child((uploadId)!!).setValue(upload)
                                    progressBar.visibility = View.INVISIBLE
                                    openImagesActivity()
                                })
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        progressBar.visibility = View.INVISIBLE
                        Toast.makeText(this@UploadActivity, e.message, Toast.LENGTH_SHORT).show()
                        Log.e("data", "${e.message}")
                    }
                    .addOnProgressListener { taskSnapshot ->
                        val progress =
                            (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                        progressBar.progress = progress.toInt()
                    }
            } else {
                Toast.makeText(this, "You haven't Selected Any file selected", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun openImagesActivity() {
        startActivity(Intent(this@UploadActivity, MainActivity::class.java))
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











