package com.example.myfirstapp.fragments

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.myfirstapp.R

class MyAdapter(private val context: Activity,private val arraylist:ArrayList<String>):ArrayAdapter<String>(context,
    R.layout.list_item,arraylist) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater:LayoutInflater= LayoutInflater.from(context)
        val view:View=inflater.inflate(R.layout.list_item,null)

        view.findViewById<TextView>(R.id.name_list).text=arraylist[position]
        return view
    }
}