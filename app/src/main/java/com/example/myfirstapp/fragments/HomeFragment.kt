package com.example.myfirstapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.myfirstapp.DbHelper
import com.example.myfirstapp.R


class HomeFragment : Fragment() {

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

        v.findViewById<TextView>(R.id.entriesCount).text=count.toString()

        return v
    }


}