package com.example.myfirstapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.google.android.gms.location.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    var latitude:String=""
    var longitude:String=""
    val PERMISSION_ID=1001
    lateinit var imagePath:String
    lateinit var myCalendar:Calendar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        var id=intent.getIntExtra("ID",0)+1
        println(id)

        var dbHelper = this?.let { it1 -> DbHelper(it1.applicationContext) }
        var db = dbHelper?.readableDatabase
        var cursor=db?.rawQuery("SELECT * FROM Users where id=?", arrayOf("$id"))

        cursor?.moveToFirst()
        var user= cursor?.let { User(it.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8)) }

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
        imagePath=user?.imagePath.toString()
        latitude=user?.latitude.toString()
        longitude=user?.longitude.toString()
        findViewById<EditText>(R.id.editLocation).setText("Longitude:  $longitude\nLatitude: $latitude")


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
        var pickImage=findViewById<Button>(R.id.editPickImage)
        pickImage.setOnClickListener{
            var intent= Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.setType("image/*")
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            startActivityForResult(intent,1)
        }


        //Fetch Location Button
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)
        findViewById<Button>(R.id.editFetchLocation).setOnClickListener{
            getLastLocation()
        }


        //Cancel Button
        findViewById<Button>(R.id.cancel).setOnClickListener{
            var intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        //Update Button

        findViewById<Button>(R.id.update).setOnClickListener{
            db= dbHelper?.readableDatabase
            var name=findViewById<EditText>(R.id.editName).text
            var address=findViewById<EditText>(R.id.editAddress).text
            var gender:String
            if(findViewById<RadioGroup>(R.id.editGender).checkedRadioButtonId == R.id.editMale)
                gender="male"
            else
                gender="female"
            var mobile_no=findViewById<EditText>(R.id.editMobileNo).text
            var dob=findViewById<EditText>(R.id.editDob).text
            db?.execSQL("Update Users Set name='${name}',gender='$gender',address='$address',mobile_no='$mobile_no',dob='$dob',imagePath='$imagePath',longitude='$longitude',latitude='$latitude' where id='$id'")
            Toast.makeText(this,"Data Updated Successfully",Toast.LENGTH_SHORT).show()
            var intent=Intent(this,MainActivity::class.java).apply {
                intent.putExtra("fragment",2)
            }
            startActivity(intent)
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
        if(requestCode==1 && resultCode== RESULT_OK)
        {
            var uri= data?.getData()
            var file = File(uri?.getPath());//create path from uri
            imagePath= file.getPath().split(":")[1]
        }
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