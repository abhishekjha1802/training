package com.example.myfirstapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible

class EditActivity : AppCompatActivity() {
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
        findViewById<TextView>(R.id.nonEditableName).setText(user?.name)
        findViewById<TextView>(R.id.nonEditableGender).setText(user?.gender)
        findViewById<TextView>(R.id.nonEditableAddress).setText(user?.address)
        findViewById<TextView>(R.id.nonEditableMobileNo).setText(user?.mobile_no)
        findViewById<TextView>(R.id.nonEditableDob).setText(user?.dob)

        findViewById<Button>(R.id.nameEditButton).setOnClickListener{
            findViewById<LinearLayout>(R.id.nonEditableNameLL).isVisible=false
            findViewById<LinearLayout>(R.id.editableNameLL).isVisible=true
            findViewById<EditText>(R.id.editableName).setText(user?.name)
        }
        findViewById<Button>(R.id.genderEditButton).setOnClickListener{
            findViewById<LinearLayout>(R.id.nonEditableGenderLL).isVisible=false
            findViewById<LinearLayout>(R.id.editableGenderLL).isVisible=true
            findViewById<EditText>(R.id.editableGender).setText(user?.gender)
        }
        findViewById<Button>(R.id.addressEditButton).setOnClickListener{
            findViewById<LinearLayout>(R.id.nonEditableAddressLL).isVisible=false
            findViewById<LinearLayout>(R.id.editableAddressLL).isVisible=true
            findViewById<EditText>(R.id.editableAddress).setText(user?.address)
        }
        findViewById<Button>(R.id.mobile_noEditButton).setOnClickListener{
            findViewById<LinearLayout>(R.id.nonEditableMobileNoLL).isVisible=false
            findViewById<LinearLayout>(R.id.editableMobileNoLL).isVisible=true
            findViewById<EditText>(R.id.editableMobileNo).setText(user?.mobile_no)
        }
        findViewById<Button>(R.id.dobEditButton).setOnClickListener{
            findViewById<LinearLayout>(R.id.nonEditableDobLL).isVisible=false
            findViewById<LinearLayout>(R.id.editableDobLL).isVisible=true
            findViewById<EditText>(R.id.editableDob).setText(user?.dob)
        }
    }
}