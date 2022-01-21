package com.example.myfirstapp.fragments

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import com.example.myfirstapp.R
import com.example.myfirstapp.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.lang.ref.Reference
import java.util.zip.Inflater

class MyAdapter(private val context: Activity,private val arraylist:ArrayList<User>) : ArrayAdapter<User>(context,
    R.layout.list_item,arraylist) {

    lateinit var storgeReference: StorageReference

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var inflater:LayoutInflater=LayoutInflater.from(context)
        var view:View=inflater.inflate(R.layout.list_item,null)

        view.findViewById<TextView>(R.id.name_list).text=arraylist[position].name
        view.findViewById<ImageView>(R.id.userImage).setImageResource(R.drawable.default_image)
        println(arraylist[position].imageUri)


        storgeReference=FirebaseStorage.getInstance().getReference("Users/${arraylist[position].id}")
        val localFile=File.createTempFile("tempImage",".jpg")
        storgeReference.getFile(localFile).addOnSuccessListener {
            val bitmap=BitmapFactory.decodeFile(localFile.absolutePath)
            view.findViewById<ImageView>(R.id.userImage).setImageBitmap(bitmap)

        }


//        var path=arraylist[position].imagePath
//        var selectedImageUri:Uri
//        println(path)
//        if(path!="null"){
//            var file=File(path)
//            selectedImageUri= Uri.fromFile(file)
//            println(selectedImageUri)
//            view.findViewById<ImageView>(R.id.userImage).setImageURI(selectedImageUri)
//        }
//        else
//        {
//
//        }

        return view
    }
}