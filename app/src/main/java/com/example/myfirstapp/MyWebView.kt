package com.example.myfirstapp

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.webkit.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase






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
    private lateinit var firebaseAuth:FirebaseAuth
    private lateinit var webViewClient:WebViewClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_web_view)

        webViewSetup()
        println(CookieManager.getInstance().getCookie("stackoverflow.com"))

    }




    private fun webViewSetup(){
        myWebView=findViewById(R.id.myWebView)

        var webSettings=myWebView.settings
        webSettings.javaScriptEnabled=true
        webSettings.setUserAgentString("com.example.myfirstapp")



        myWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                view?.loadUrl(url)
                return true
            }
        }

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

        @JavascriptInterface
        fun signIn(){
            checkUser()
            runOnUiThread(Runnable {
                myWebView.evaluateJavascript("javascript: signInResult(\"${firebaseAuth.currentUser?.email.toString()}\");",null);
            })
        }
    }

    private fun sendDataToWebView(){
        myWebView.evaluateJavascript("javascript: getResult(\"$brand\",\"$model\",\"$id\",\"$sdk\",\"$manufacture\",\"$user\",\"$type\",\"$base\",\"$versionCode\");",null);

    }


    private fun checkUser(){
        firebaseAuth=FirebaseAuth.getInstance()
        var firebaseUser=firebaseAuth.currentUser
        if(firebaseUser==null)
        {
            var intent= Intent(this,SignIn::class.java)
            startActivity(intent)
        }
        else
        {

        }
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


    var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (myWebView.canGoBack()) {
                myWebView.goBack()
        } else {
            super.onBackPressed()
        }
    }

}

