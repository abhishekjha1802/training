package com.example.myfirstapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.AccessControlContext

class DbHelper(context: Context): SQLiteOpenHelper(context,"UserDb",null,1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("Create Table Users(id Integer PRIMARY KEY AUTOINCREMENT,name Text,gender Text,address Text,mobile_no Text,dob Text,imagePath Text,longitude Text,latitude Text)")

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

}