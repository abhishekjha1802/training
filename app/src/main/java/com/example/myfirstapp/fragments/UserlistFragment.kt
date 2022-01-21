package com.example.myfirstapp.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.example.myfirstapp.DbHelper
import com.example.myfirstapp.EditActivity
import com.example.myfirstapp.R
import com.example.myfirstapp.User

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
        var cursor=db?.query("Users",null,null,null,null,null,"modificationTime DESC")
        var count: Int? =cursor?.count

        var userArray=ArrayList<User>()




        while (cursor?.moveToNext() == true)
        {
            var user=User(cursor.getInt(0),cursor.getString(1),"","","","",cursor.getString(6),"","")
            userArray.add(user)
        }


        var listview:ListView=v.findViewById<ListView>(R.id.list_view)
        listview.adapter=MyAdapter(requireActivity(),userArray)


        listview.onItemClickListener= object :AdapterView.OnItemClickListener{
            override fun onItemClick(
                p0: AdapterView<*>?,
                p1: View?,
                p2: Int,
                p3: Long
            ) {
                println(userArray[p2].name)
                var intent=Intent(requireContext(),EditActivity::class.java).apply{
                    putExtra("ID",userArray[p2].id)
                }
                startActivity(intent)

            }
        }

        return v
    }


}




