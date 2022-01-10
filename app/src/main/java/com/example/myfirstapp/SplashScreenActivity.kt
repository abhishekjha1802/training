package com.example.myfirstapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.core.graphics.alpha

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        var logo=findViewById<ImageView>(R.id.toothsi_logo)
        logo.alpha = 0f
        logo.animate().setDuration(5000).alpha(1f).withEndAction{
            val i= Intent(this,MainActivity::class.java)
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()

        }



    }

}