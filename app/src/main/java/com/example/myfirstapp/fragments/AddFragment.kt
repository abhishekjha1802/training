package com.example.myfirstapp.fragments


import android.annotation.SuppressLint
import android.app.Activity.LOCATION_SERVICE
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.myfirstapp.MainActivity
import com.example.myfirstapp.R
import com.example.myfirstapp.User
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest.create
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class AddFragment : Fragment() {

    //Ui Variables----------------------------------->
    lateinit var name:String
    lateinit var mobile_no:String
    lateinit var address:String
    lateinit var gender:String
    lateinit var dob :String
    lateinit var vw: View
    lateinit var myCalendar:Calendar
    var imageUri:String=""

    //location and Image variables------------------------------>
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    var latitude:String=""
    var longitude:String=""
    val PERMISSION_ID=1001
    val REQUEST_SINGLE_FILE=100
    lateinit var selectedImageUri:Uri

    //Firebase Variables----------------------------------------->
    lateinit var auth:FirebaseAuth
    lateinit var databaseReference: DatabaseReference
    lateinit var storageReference:StorageReference
    lateinit var user:User
    lateinit var uid:String
    lateinit var dialog: Dialog
    var uniqueId:String=""



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        vw = inflater.inflate(R.layout.fragment_add, container, false)
        loadState(vw)


        selectedImageUri = Uri.parse("android.resource://com.example.myfirstapp/drawable/default_image")
        //Setting Date Picker---------------------------------------->
        myCalendar=Calendar.getInstance()
        val datPicker=DatePickerDialog.OnDateSetListener{ datePicker: DatePicker, year: Int, month: Int, date_of_month: Int ->
            myCalendar.set(Calendar.YEAR,year)
            myCalendar.set(Calendar.MONTH,month)
            myCalendar.set(Calendar.DAY_OF_MONTH,date_of_month)
            updateLabel(myCalendar)
        }
        vw.findViewById<TextView>(R.id.dob).setOnClickListener{
            DatePickerDialog(requireContext(),datPicker,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        //Pick Image Button----------------------------------------->
        var pickImage=vw.findViewById<ImageView>(R.id.pickImage)
        pickImage.setOnClickListener{
            var intent = Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_SINGLE_FILE);
        }



        //Fetch Location Button-------------------------------------------->
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(requireActivity())
        vw.findViewById<ImageView>(R.id.fetchLocation).setOnClickListener{
            getLastLocation()
        }


        //Setting Firebas--------------------------------------------------->
        auth= FirebaseAuth.getInstance()
        uid=auth.currentUser?.uid.toString()
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
        val formatted = current.format(formatter)
        uniqueId= uid+formatted
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users")


        //Submit Button------------------------------------------------------>
        var submit = vw.findViewById<Button>(R.id.submit)
        submit.setOnClickListener {

            name = vw.findViewById<EditText>(R.id.name).text.toString()
            mobile_no = vw.findViewById<EditText>(R.id.mobile_no).text.toString()
            address = vw.findViewById<EditText>(R.id.address).text.toString()
            var id = vw.findViewById<RadioGroup>(R.id.gender).checkedRadioButtonId
            if (id == R.id.male)
                gender = "male"
            else
                gender = "female"
            dob = vw.findViewById<EditText>(R.id.dob).text.toString()

            user=User(
                uniqueId,
                name,
                gender,
                address,
                mobile_no,
                dob,
                imageUri,
                longitude,
                latitude
            )

            //Firebase Code------------------------------------------------------>
            showProgressBar()

            if(uid!=null) {
                databaseReference.child(uniqueId).setValue(user)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            uploadProfilePic()
                        } else {
                            hideProgressBar()
                            Toast.makeText(
                                requireContext(),
                                "Some Error Occured",
                                Toast.LENGTH_SHORT
                            )
                        }
                    }
            }
            //-------------------------------------------------------------------->

            reset(vw)
            saveState(vw)
            loadState(vw)

            Toast.makeText(
                context?.applicationContext,
                "Data Saved Successfully",
                Toast.LENGTH_LONG
            ).show()

            var intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
        return vw
    }

    private fun updateLabel(calendar: Calendar)
    {
        val myFormat="dd-MM-yyy"
        val sdf=SimpleDateFormat(myFormat,Locale.UK)
        vw.findViewById<EditText>(R.id.dob).setText(sdf.format(myCalendar.time))
    }


    //Firebase Functions------------------------------------------------------------------------------->
    private fun uploadProfilePic() {
        storageReference= FirebaseStorage.getInstance().getReference("Users/"+uniqueId)
        storageReference.putFile(selectedImageUri).addOnSuccessListener {
            hideProgressBar()
            Toast.makeText(requireContext(),"Successfully Added",Toast.LENGTH_SHORT)
        }.addOnFailureListener{
            hideProgressBar()
            Toast.makeText(requireContext(),"Failed To Add",Toast.LENGTH_SHORT)
        }
    }

    private fun showProgressBar(){
        dialog= Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideProgressBar(){
        dialog.dismiss()
    }

    //Submit Button Functions-------------------------------------------------------->
    private fun reset(view:View) {
        view.findViewById<EditText>(R.id.name).text.clear()
        view.findViewById<EditText>(R.id.mobile_no).text.clear()
        view.findViewById<EditText>(R.id.address).text.clear()
        view.findViewById<RadioGroup>(R.id.gender).check(R.id.male)
        view.findViewById<EditText>(R.id.dob).text.clear()
        imageUri=""
        longitude=""
        latitude=""
    }


    private fun loadState(view:View) {
        val sharedPreferences= context?.getSharedPreferences("sharedPref", MODE_PRIVATE)
        view.findViewById<EditText>(R.id.name).setText(sharedPreferences?.getString("name",null))
        view.findViewById<EditText>(R.id.address).setText(sharedPreferences?.getString("address",null))
        view.findViewById<EditText>(R.id.dob).setText(sharedPreferences?.getString("dob",null))
        view.findViewById<EditText>(R.id.mobile_no).setText(sharedPreferences?.getString("mobile_no",null))
        imageUri= sharedPreferences?.getString("imagePath",null).toString()
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
        editable?.putString("imagePath",imageUri)
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
        if(resultCode==RESULT_OK)
        {
            if(requestCode==REQUEST_SINGLE_FILE)
            {
                selectedImageUri = data?.getData()!!;
                imageUri=selectedImageUri.toString()
            }
        }
        vw.findViewById<ImageView>(R.id.pickImage).setImageURI(selectedImageUri)
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

            if(isLocationEnabled()){


                fusedLocationProviderClient.lastLocation.addOnCompleteListener{
                    var location=it.result
                    if(location==null)
                    {
                        newLoction()
                    }
                    else
                    {
                        longitude=location.longitude.toString()
                        latitude=location.latitude.toString()
                        vw.findViewById<EditText>(R.id.location).setText("Longitude:  $longitude\nLatitude:  $latitude")

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
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,locationCallback,Looper.myLooper()
        )
    }

    private var locationCallback= object:LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            var lastlocation: Location =p0.lastLocation
            longitude=lastlocation.longitude.toString()
            latitude=lastlocation.latitude.toString()
            vw.findViewById<EditText>(R.id.location).setText("Longitude:  $longitude\nLatitude: $latitude")
        }
    }
}