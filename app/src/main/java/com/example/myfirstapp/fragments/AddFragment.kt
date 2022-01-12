package com.example.myfirstapp.fragments

import android.content.ContentValues
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.myfirstapp.DbHelper
import com.example.myfirstapp.MainActivity
import com.example.myfirstapp.R


class AddFragment : Fragment() {

    lateinit var name:String
    lateinit var mobile_no:String
    lateinit var address:String
    lateinit var gender:String
    lateinit var dob :String
    lateinit var vw: View



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        vw = inflater.inflate(R.layout.fragment_add, container, false)
        loadState(vw)
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
    private fun reset(view:View) {
        view.findViewById<EditText>(R.id.name).text.clear()
        view.findViewById<EditText>(R.id.mobile_no).text.clear()
        view.findViewById<EditText>(R.id.address).text.clear()
        view.findViewById<RadioGroup>(R.id.gender).check(R.id.male)
        view.findViewById<EditText>(R.id.dob).text.clear()
    }


    private fun loadState(view:View) {
        val sharedPreferences= context?.getSharedPreferences("sharedPref", MODE_PRIVATE)
        view.findViewById<EditText>(R.id.name).setText(sharedPreferences?.getString("name",null))
        view.findViewById<EditText>(R.id.address).setText(sharedPreferences?.getString("address",null))
        view.findViewById<EditText>(R.id.dob).setText(sharedPreferences?.getString("dob",null))
        view.findViewById<EditText>(R.id.mobile_no).setText(sharedPreferences?.getString("mobile_no",null))
        var radioButtonId=sharedPreferences?.getInt("genderId",0)
        if(radioButtonId==0)
            view.findViewById<RadioGroup>(R.id.gender).check(R.id.male)
        else
            radioButtonId?.let { view.findViewById<RadioGroup>(R.id.gender).check(it) }
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
        if(id==R.id.male){
            editable?.putInt("genderId",R.id.male)
        }
        else{
            editable?.putInt("genderId",R.id.female)
        }

        editable?.apply()
    }
}