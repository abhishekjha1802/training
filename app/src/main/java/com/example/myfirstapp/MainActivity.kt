package com.example.myfirstapp

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.*
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    lateinit var name:String
    lateinit var mobile_no:String
    lateinit var address:String
    lateinit var gender:String
    lateinit var dob :String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadState()


        var submit = findViewById<Button>(R.id.submit)
        submit.setOnClickListener {
            var dbHelper=DbHelper(applicationContext)
            var db=dbHelper.writableDatabase

            name = findViewById<EditText>(R.id.name).text.toString()
            mobile_no = findViewById<EditText>(R.id.mobile_no).text.toString()
            address = findViewById<EditText>(R.id.address).text.toString()
            var id = findViewById<RadioGroup>(R.id.gender).checkedRadioButtonId
            if(id==R.id.male)
                gender="male"
            else
                gender="female"
            dob = findViewById<EditText>(R.id.dob).text.toString()

            var cv=ContentValues()
            cv.put("name",name)
            cv.put("gender",gender)
            cv.put("address",address)
            cv.put("dob",dob)
            cv.put("mobile_no",mobile_no)
            db.insert("Users",null,cv)


            reset()
            saveState()
            loadState()
            Toast.makeText(applicationContext,"Data Saved Successfully",Toast.LENGTH_LONG).show()
        }
        var reset = findViewById<Button>(R.id.reset)
        reset.setOnClickListener {

            reset()
        }
    }

    private fun reset() {
        findViewById<EditText>(R.id.name).text.clear()
        findViewById<EditText>(R.id.mobile_no).text.clear()
        findViewById<EditText>(R.id.address).text.clear()
        findViewById<RadioGroup>(R.id.gender).check(R.id.male)
        findViewById<EditText>(R.id.dob).text.clear()
    }


    private fun loadState() {
        val sharedPreferences=getSharedPreferences("sharedPref", MODE_PRIVATE)
        findViewById<EditText>(R.id.name).setText(sharedPreferences.getString("name",null))
        findViewById<EditText>(R.id.address).setText(sharedPreferences.getString("address",null))
        findViewById<EditText>(R.id.dob).setText(sharedPreferences.getString("dob",null))
        findViewById<EditText>(R.id.mobile_no).setText(sharedPreferences.getString("mobile_no",null))
        var radioButtonId=sharedPreferences.getInt("genderId",0)
        if(radioButtonId==0)
            findViewById<RadioGroup>(R.id.gender).check(R.id.male)
        else
            findViewById<RadioGroup>(R.id.gender).check(radioButtonId)
    }

    override fun onStop() {
        super.onStop()
        saveState()
    }

    private fun saveState() {
        name = findViewById<EditText>(R.id.name).text.toString()
        mobile_no = findViewById<EditText>(R.id.mobile_no).text.toString()
        address = findViewById<EditText>(R.id.address).text.toString()
        var id = findViewById<RadioGroup>(R.id.gender).checkedRadioButtonId
        dob = findViewById<EditText>(R.id.dob).text.toString()

        val sharedPreferences=getSharedPreferences("sharedPref", MODE_PRIVATE)
        var editable=sharedPreferences.edit()
        editable.putString("name",name)
        editable.putString("mobile_no",mobile_no)
        editable.putString("address",address)
        editable.putString("dob",dob)
        if(id==R.id.male){
            editable.putInt("genderId",R.id.male)
        }
        else{
            editable.putInt("genderId",R.id.female)
        }

        editable.apply()
    }


}
