package com.example.myfirstapp

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.webkit.JsResult

import android.webkit.WebChromeClient




class MyWebView : AppCompatActivity() {
    private lateinit var myWebView: WebView
    private lateinit var mEditText:EditText
    private lateinit var brand:String
    private lateinit var model:String
    private lateinit var id:String
    private lateinit var manufacture:String
    private lateinit var sdk:String
    private lateinit var user:String
    private lateinit var type:String
    private lateinit var base:String
    private lateinit var versionCode:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_web_view)

        webViewSetup()

    }

    private fun webViewSetup(){
        myWebView=findViewById(R.id.myWebView)

        var webSettings=myWebView.settings
        webSettings.javaScriptEnabled=true
        myWebView.addJavascriptInterface(JSBridge(this),"JSBridge")
        myWebView.loadUrl("file:///android_asset/index.html")


        myWebView.webChromeClient = object : WebChromeClient() {
            //Other methods for your WebChromeClient here, if needed..
            override fun onJsAlert(
                view: WebView,
                url: String,
                message: String,
                result: JsResult
            ): Boolean {
                return super.onJsAlert(view, url, message, result)
            }
        }
    }

    inner class JSBridge(val context: Context){
        @JavascriptInterface
        fun sendRequest(){
            Toast.makeText(context,"Request Received", Toast.LENGTH_LONG).show()
            getDeviceInfo()


            runOnUiThread(Runnable {
                sendDataToWebView()
            })

        }
    }

    private fun sendDataToWebView(){
        myWebView.evaluateJavascript("javascript: getResult(\"$brand\",\"$model\",\"$id\",\"$sdk\",\"$manufacture\",\"$user\",\"$type\",\"$base\",\"$versionCode\");",null);

    }

    private fun getDeviceInfo(){
        brand= "Brand: ${Build.BRAND}  "
        model="Model: ${Build.MODEL} "
        id="ID: ${Build.ID} "
        sdk="SDK: ${Build.VERSION.SDK_INT} "
        manufacture="Manufacture: ${Build.MANUFACTURER} "
        user="User: ${Build.USER} "
        type="Type: ${Build.TYPE} "
        base="Base: ${Build.VERSION_CODES.BASE} "
        versionCode="Version Code: ${Build.VERSION.RELEASE}"
    }


    override fun onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack()
        } else {
            super.onBackPressed()
        }
    }

}

