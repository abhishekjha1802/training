package com.example.myfirstapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isVisible
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class EditActivity : AppCompatActivity() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    var latitude:String=""
    var longitude:String=""
    lateinit var imageUri:String
    val PERMISSION_ID=1001
    val REQUEST_SINGLE_FILE=100
    lateinit var myCalendar:Calendar

    //Firebase Variables
    lateinit var auth:FirebaseAuth
    lateinit var databaseReference:DatabaseReference
    lateinit var storageReference:StorageReference
    lateinit var selectedImageUri: Uri
    lateinit var user:User




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        user=intent.extras?.get("user") as User
        println(user)

//        var dbHelper = this?.let { it1 -> DbHelper(it1.applicationContext) }
//        var db = dbHelper?.readableDatabase
//        var cursor=db?.rawQuery("SELECT * FROM Users where id=?", arrayOf("$id"))
//
//        cursor?.moveToFirst()
//        var user= cursor?.let { User(it.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8)) }

        findViewById<EditText>(R.id.editName).setText(user?.name)
        findViewById<EditText>(R.id.editMobileNo).setText(user?.mobile_no)
        findViewById<EditText>(R.id.editAddress).setText(user?.address)
        findViewById<EditText>(R.id.editDob).setText(user?.dob)
        var genderId:Int
        if(user?.gender=="male")
            genderId=R.id.editMale
        else
            genderId=R.id.editFemale
        findViewById<RadioGroup>(R.id.editGender).check(genderId)
        imageUri=user?.imageUri.toString()
        latitude=user?.latitude.toString()
        longitude=user?.longitude.toString()
        findViewById<EditText>(R.id.editLocation).setText("Longitude:  $longitude\nLatitude: $latitude")
        storageReference=FirebaseStorage.getInstance().getReference("Users/${user.id}")
        val localFile=File.createTempFile("tempImage",".jpg")
        storageReference.getFile(localFile).addOnSuccessListener {
            val bitmap= BitmapFactory.decodeFile(localFile.absolutePath)
            findViewById<ImageView>(R.id.editPickImage).setImageBitmap(bitmap)

        }



        //Setting Date Picker
        myCalendar= Calendar.getInstance()
        val datPicker=
            DatePickerDialog.OnDateSetListener{ datePicker: DatePicker, year: Int, month: Int, date_of_month: Int ->
            myCalendar.set(Calendar.YEAR,year)
            myCalendar.set(Calendar.MONTH,month)
            myCalendar.set(Calendar.DAY_OF_MONTH,date_of_month)
            updateLabel(myCalendar)
        }
        findViewById<TextView>(R.id.editDob).setOnClickListener{
            DatePickerDialog(this,datPicker,myCalendar.get(Calendar.YEAR),myCalendar.get(
                Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }


        //Fetch Image Button
        var pickImage=findViewById<ImageView>(R.id.editPickImage)
        pickImage.setOnClickListener{
            var intent = Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_SINGLE_FILE);

        }


        //Fetch Location Button
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)
        findViewById<ImageView>(R.id.editFetchLocation).setOnClickListener{
            getLastLocation()
        }


        //Cancel Button
        findViewById<Button>(R.id.cancel).setOnClickListener{
            var intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        //Update Button

        findViewById<Button>(R.id.update).setOnClickListener{
            var name=findViewById<EditText>(R.id.editName).text.toString()
            var address=findViewById<EditText>(R.id.editAddress).text.toString()
            var gender:String
            if(findViewById<RadioGroup>(R.id.editGender).checkedRadioButtonId == R.id.editMale)
                gender="male"
            else
                gender="female"
            var mobile_no=findViewById<EditText>(R.id.editMobileNo).text.toString()
            var dob=findViewById<EditText>(R.id.editDob).text.toString()

            var tempUser=User(user.id,name,gender,address,mobile_no,dob,imageUri,longitude,latitude)

            //Firebase code
            auth= FirebaseAuth.getInstance()
            databaseReference= FirebaseDatabase.getInstance().getReference().child("Users")
            databaseReference.child(user.id).setValue(tempUser).addOnCompleteListener{
                if (it.isSuccessful) {
                    if(imageUri!=user.imageUri)
                        uploadProfilePic()
                } else {
                    //hideProgressBar()
                    Toast.makeText(
                        this,
                        "Some Error Occured",
                        Toast.LENGTH_SHORT
                    )
                }
            }






            Toast.makeText(this,"Data Updated Successfully",Toast.LENGTH_SHORT).show()
            var intent=Intent(this,MainActivity::class.java).apply {
                intent.putExtra("fragment",2)
            }
            startActivity(intent)
        }


    }

    private fun uploadProfilePic() {
        storageReference= FirebaseStorage.getInstance().getReference("Users/"+user.id)
        storageReference.putFile(selectedImageUri).addOnSuccessListener {
            //hideProgressBar()
            Toast.makeText(this,"Successfully Added",Toast.LENGTH_SHORT)
        }.addOnFailureListener{
            //hideProgressBar()
            Toast.makeText(this,"Failed To Add",Toast.LENGTH_SHORT)
        }
    }


    private fun updateLabel(calendar: Calendar)
    {
        val myFormat="dd-MM-yyy"
        val sdf= SimpleDateFormat(myFormat,Locale.UK)
        findViewById<EditText>(R.id.editDob).setText(sdf.format(myCalendar.time))
    }

    //Pick Image Fuctions
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== RESULT_OK)
        {
            if(requestCode==REQUEST_SINGLE_FILE)
            {
                selectedImageUri = data?.getData()!!;
                imageUri=selectedImageUri.toString()
            }
        }
        findViewById<ImageView>(R.id.editPickImage).setImageURI(selectedImageUri)
    }

    //Fetch Location Functions
    private fun checkPermission():Boolean{
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)  {
            return true
        }
        return false
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),PERMISSION_ID)
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==PERMISSION_ID)
        {
            if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                println("permission granted")
            }
        }
    }


    private fun isLocationEnabled():Boolean{
        var locationManager: LocationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    private fun getLastLocation(){
        if(checkPermission()){
            println("Permission is given")
            if(isLocationEnabled()){
                println("location is enabled")

                fusedLocationProviderClient.lastLocation.addOnCompleteListener{
                    var location=it.result
                    if(location==null)
                    {
                        println("Requesting new location")
                        newLoction()
                    }
                    else
                    {
                        println("Assigning Loaction")
                        longitude=location.longitude.toString()
                        latitude=location.latitude.toString()
                        findViewById<EditText>(R.id.editLocation).setText("Longitude:  $longitude\nLatitude: $latitude")
                    }
                }
            }
            else
            {
                Toast.makeText(this,"Please turn on location",Toast.LENGTH_SHORT)
            }
        }
        else{
            requestPermission()
        }

    }

    @SuppressLint("MissingPermission")
    private fun newLoction(){

        locationRequest= LocationRequest.create().setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(0).setFastestInterval(0).setNumUpdates(2)
        println("Location Request Set")
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,locationCallback, Looper.myLooper()
        )
        println("New Location assigned")
    }

    private var locationCallback= object: LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            println("Under Location Callback")
            var lastlocation: Location =p0.lastLocation
            longitude=lastlocation.longitude.toString()
            latitude=lastlocation.latitude.toString()
            findViewById<EditText>(R.id.editLocation).setText("Longitude:  $longitude\nLatitude: $latitude")
        }
    }
}