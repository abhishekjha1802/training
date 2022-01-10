package com.example.myfirstapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        var submit=findViewById<Button>(R.id.submit)
        submit.setOnClickListener{
            var name=findViewById<EditText>(R.id.name).text.toString()
            var mobile_no=findViewById<EditText>(R.id.mobile_no).text.toString()
            var address=findViewById<EditText>(R.id.address).text.toString()
            var id=findViewById<RadioGroup>(R.id.gender).checkedRadioButtonId
            var gender=findViewById<RadioButton>(id).text.toString()
            var dob=findViewById<EditText>(R.id.dob).text.toString()

            var message="Name:  "+name+"\n"+"Mobile No:  "+mobile_no+"\nAddress:  "+address+"\nGender:  "+gender+"\nDate Of Birth:  "+dob
            AlertDialog.Builder(this)
                .setTitle("Details")
                .setMessage(message)
                .setPositiveButton("ok"){
                    _,_->
                    //do nothing
                }.show()
        }
    }
}