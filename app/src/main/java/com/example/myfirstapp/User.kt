package com.example.myfirstapp

import android.text.Editable
import java.io.Serializable

data class User(
    var id:String="",
    var name: String ="",
    var gender:String="",
    var address:String="",
    var mobile_no:String="",
    var dob:String="",
    var imageUri:String="",
    var longitude:String="",
    var latitude:String=""
):Serializable