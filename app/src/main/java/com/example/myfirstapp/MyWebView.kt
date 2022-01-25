package com.example.myfirstapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView

class MyWebView : AppCompatActivity() {
    private lateinit var myWebView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_web_view)

        webViewSetup()
    }

    private fun webViewSetup(){
        myWebView=findViewById(R.id.myWebView)

        var webSettings=myWebView.settings
        webSettings.javaScriptEnabled=true
        myWebView.loadUrl("file:///android_asset/index.html")
    }
}