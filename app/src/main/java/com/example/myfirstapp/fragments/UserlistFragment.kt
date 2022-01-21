package com.example.myfirstapp.fragments

import android.app.Activity
import android.app.Dialog
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference

class UserlistFragment : Fragment() {

    lateinit var v:View
    lateinit var auth:FirebaseAuth
    lateinit var dialog: Dialog
    lateinit var databaseReference: DatabaseReference
    lateinit var storageReference: StorageReference
    lateinit var uid:String
    var maxId=1
    lateinit var userArray:ArrayList<User>

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v=inflater.inflate(R.layout.fragment_userlist,container,false)
        super.onCreateView(inflater, container, savedInstanceState)

        //Firebase Code----------------------------------------------------->
        auth= FirebaseAuth.getInstance()
        uid= auth.currentUser.toString()
        userArray= ArrayList()

        databaseReference=FirebaseDatabase.getInstance().getReference().child("Users")
        if(uid.isNotEmpty())
        {
            getUsersData()
        }




        return v
    }

    private fun getUsersData() {

       databaseReference.addValueEventListener(object :ValueEventListener{
           override fun onDataChange(snapshot: DataSnapshot) {
               for(data in snapshot.children){
                    var model=data.getValue(User::class.java)
                    userArray.add(model as User)

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
                           putExtra("user",userArray[p2])
                       }
                       startActivity(intent)

                   }
               }

           }

           override fun onCancelled(error: DatabaseError) {
               TODO("Not yet implemented")
           }

       })
    }


}




