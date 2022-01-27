package com.example.myfirstapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MyWebView : AppCompatActivity() {
    private lateinit var myWebView: WebView
    private lateinit var mEditText:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_web_view)
        mEditText=findViewById(R.id.editTextAV)

        webViewSetup()

        findViewById<Button>(R.id.sendToWV).setOnClickListener{
            sendDataToWebView()
        }
    }

    private fun webViewSetup(){
        myWebView=findViewById(R.id.myWebView)

        var webSettings=myWebView.settings
        webSettings.javaScriptEnabled=true
        myWebView.addJavascriptInterface(JSBridge(this,findViewById(R.id.editTextAV)),"JSBridge")
        myWebView.loadUrl("file:///android_asset/index.html")
    }

    class JSBridge(val context: Context, val editTextInput: EditText){
        @JavascriptInterface
        fun showMessageInNative(message:String){
            Toast.makeText(context,"Data Send To Native", Toast.LENGTH_LONG).show()
            editTextInput.setText(message)
        }
    }

    private fun sendDataToWebView(){
        myWebView.evaluateJavascript(
            "javascript: " +"updateFromNative(\"" + mEditText.text + "\")",
            null)
    }
}

