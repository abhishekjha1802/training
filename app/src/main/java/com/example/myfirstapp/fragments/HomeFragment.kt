package com.example.myfirstapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.myfirstapp.DbHelper
import com.example.myfirstapp.R
import com.example.myfirstapp.SignIn
import com.google.firebase.auth.FirebaseAuth


class HomeFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var v=inflater.inflate(R.layout.fragment_home,container,false)
        super.onCreateView(inflater, container, savedInstanceState)
        var dbHelper = context?.let { it1 -> DbHelper(it1.applicationContext) }
        var db = dbHelper?.readableDatabase
        var cursor=db?.query("Users",null,null,null,null,null,null)
        var count=cursor?.count

        firebaseAuth= FirebaseAuth.getInstance()
        checkUser()
        v.findViewById<TextView>(R.id.entriesCount).text=count.toString()


        v.findViewById<Button>(R.id.logout).setOnClickListener{
            firebaseAuth.signOut()
            checkUser()
        }
        return v
    }

    private fun checkUser(){
        var firebaseUser=firebaseAuth.currentUser
        if(firebaseUser==null)
        {
            var intent=Intent(requireContext(),SignIn::class.java)
            startActivity(intent)
        }
        else
        {

        }
    }


}