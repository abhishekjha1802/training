package com.example.myfirstapp.fragments


import android.annotation.SuppressLint
import android.app.Activity.LOCATION_SERVICE
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import com.example.myfirstapp.DbHelper
import com.example.myfirstapp.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest.create
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.io.File



class AddFragment : Fragment() {

    lateinit var name:String
    lateinit var mobile_no:String
    lateinit var address:String
    lateinit var gender:String
    lateinit var dob :String
    lateinit var vw: View
    var imagePath:String=""
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    var latitude:String=""
    var longitude:String=""
    val PERMISSION_ID=1001



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        vw = inflater.inflate(R.layout.fragment_add, container, false)
        loadState(vw)

        //Pick Image Button----------------------------------------->

        var pickImage=vw.findViewById<Button>(R.id.pickImage)
        pickImage.setOnClickListener{
            var intent=Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.setType("image/*")
            intent.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            startActivityForResult(intent,1)

        }



        //Fetch Location Button-------------------------------------------->
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(requireActivity())
        vw.findViewById<Button>(R.id.fetchLocation).setOnClickListener{
            getLastLocation()
        }



        //Submit Button------------------------------------------------------>
        var submit = vw.findViewById<Button>(R.id.submit)
        submit.setOnClickListener {
            var dbHelper = context?.let { it1 -> DbHelper(it1.applicationContext) }
            var db = dbHelper?.writableDatabase

            name = vw.findViewById<EditText>(R.id.name).text.toString()
            mobile_no = vw.findViewById<EditText>(R.id.mobile_no).text.toString()
            address = vw.findViewById<EditText>(R.id.address).text.toString()
            var id = vw.findViewById<RadioGroup>(R.id.gender).checkedRadioButtonId
            if (id == R.id.male)
                gender = "male"
            else
                gender = "female"
            dob = vw.findViewById<EditText>(R.id.dob).text.toString()

            var cv = ContentValues()
            cv.put("name", name)
            cv.put("gender", gender)
            cv.put("address", address)
            cv.put("dob", dob)
            cv.put("mobile_no", mobile_no)
            cv.put("imagePath",imagePath)
            cv.put("longitude",longitude)
            cv.put("latitude",latitude)
            db?.insert("Users", null, cv)


            reset(vw)
            saveState(vw)
            loadState(vw)

            Toast.makeText(
                context?.applicationContext,
                "Data Saved Successfully",
                Toast.LENGTH_LONG
            ).show()
        }
        return vw
    }

    //Submit Button Functions-------------------------------------------------------->
    private fun reset(view:View) {
        view.findViewById<EditText>(R.id.name).text.clear()
        view.findViewById<EditText>(R.id.mobile_no).text.clear()
        view.findViewById<EditText>(R.id.address).text.clear()
        view.findViewById<RadioGroup>(R.id.gender).check(R.id.male)
        view.findViewById<EditText>(R.id.dob).text.clear()
        imagePath=""
        longitude=""
        latitude=""
    }


    private fun loadState(view:View) {
        val sharedPreferences= context?.getSharedPreferences("sharedPref", MODE_PRIVATE)
        view.findViewById<EditText>(R.id.name).setText(sharedPreferences?.getString("name",null))
        view.findViewById<EditText>(R.id.address).setText(sharedPreferences?.getString("address",null))
        view.findViewById<EditText>(R.id.dob).setText(sharedPreferences?.getString("dob",null))
        view.findViewById<EditText>(R.id.mobile_no).setText(sharedPreferences?.getString("mobile_no",null))
        imagePath= sharedPreferences?.getString("imagePath",null).toString()
        var radioButtonId=sharedPreferences?.getInt("genderId",0)
        if(radioButtonId==0)
            view.findViewById<RadioGroup>(R.id.gender).check(R.id.male)
        else
            radioButtonId?.let { view.findViewById<RadioGroup>(R.id.gender).check(it) }
        longitude= sharedPreferences?.getString("longitude","").toString()
        latitude= sharedPreferences?.getString("latitude","").toString()

    }

    override fun onStop() {
        super.onStop()
        saveState(vw)
    }

    private fun saveState(view:View) {
        name = view.findViewById<EditText>(R.id.name).text.toString()
        mobile_no = view.findViewById<EditText>(R.id.mobile_no).text.toString()
        address = view.findViewById<EditText>(R.id.address).text.toString()
        var id = view.findViewById<RadioGroup>(R.id.gender).checkedRadioButtonId
        dob = view.findViewById<EditText>(R.id.dob).text.toString()


        val sharedPreferences=context?.getSharedPreferences("sharedPref", MODE_PRIVATE)
        var editable=sharedPreferences?.edit()
        editable?.putString("name",name)
        editable?.putString("mobile_no",mobile_no)
        editable?.putString("address",address)
        editable?.putString("dob",dob)
        editable?.putString("imagePath",imagePath)
        if(id==R.id.male){
            editable?.putInt("genderId",R.id.male)
        }
        else{
            editable?.putInt("genderId",R.id.female)
        }
        editable?.putString("longitude",longitude)
        editable?.putString("latitude",latitude)
        editable?.apply()
    }

    //Functions for Image-------------------------------------------------->

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1 && resultCode==RESULT_OK)
        {
            var uri= data?.getData()
            var file = File(uri?.getPath());//create path from uri
            imagePath= file.getPath().split(":")[1]
            vw.findViewById<TextView>(R.id.imagePathUrl).setText(imagePath)
        }
    }

    //Functions for Location------------------------------------------------>

    private fun checkPermission():Boolean{
        if(ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)  {
            return true
        }
        return false
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),PERMISSION_ID)
    }

     override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
         if(requestCode==PERMISSION_ID)
         {
             if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                 println("permission granted")
             }
         }
     }


    private fun isLocationEnabled():Boolean{
        var locationManager:LocationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
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
                        vw.findViewById<TextView>(R.id.locationText).setText("longitude: ${longitude} \n latitude: ${latitude}")

                    }
                }
            }
            else
            {
                Toast.makeText(requireActivity(),"Please turn on location",Toast.LENGTH_SHORT)
            }
        }
        else{
            requestPermission()
        }

    }

    @SuppressLint("MissingPermission")
    private fun newLoction(){

        locationRequest=create().setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(0).setFastestInterval(0).setNumUpdates(2)
        println("Location Request Set")
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,locationCallback,Looper.myLooper()
        )
        println("New Location assigned")
    }

    private var locationCallback= object:LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            println("Under Location Callback")
            var lastlocation: Location =p0.lastLocation
            longitude=lastlocation.longitude.toString()
            latitude=lastlocation.latitude.toString()
            vw.findViewById<TextView>(R.id.locationText).setText("longitude: ${longitude} \n latitude: ${latitude}")
        }
    }
}