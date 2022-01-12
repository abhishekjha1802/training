package com.example.myfirstapp.fragments

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.example.myfirstapp.DbHelper
import com.example.myfirstapp.R

class UserlistFragment : Fragment() {

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var v=inflater.inflate(R.layout.fragment_userlist,container,false)
        super.onCreateView(inflater, container, savedInstanceState)
        var dbHelper = context?.let { it1 -> DbHelper(it1.applicationContext) }
        var db = dbHelper?.readableDatabase
        var cursor=db?.query("Users",null,null,null,null,null,null)
        var count: Int? =cursor?.count

        var nameArray=ArrayList<String>()
        var genderArray=ArrayList<String>()
        var addressArray=ArrayList<String>()
        var mobile_noArray=ArrayList<String>()
        var dobArray=ArrayList<String>()

        if(cursor!=null)
        {
            cursor.moveToFirst()
        }

        do{
            cursor?.let { nameArray.add(it?.getString(1)) }
        }
        while (cursor?.moveToNext() == true)

        var listview=v.findViewById<ListView>(R.id.list_view)
        listview.adapter=MyAdapter(requireActivity(),nameArray)


        return v
    }


}