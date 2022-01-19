package com.example.myfirstapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myfirstapp.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.oAuthProvider
import java.sql.DriverManager.println

class SignIn : AppCompatActivity() {

    private lateinit var binding:ActivitySignInBinding

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth:FirebaseAuth

    private companion object{
        private const val RC_SIGN_IN=100
        private const val TAG="GOOGLE_SIGN_IN_TAG"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var googleSignInOptions=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient=GoogleSignIn.getClient(this,googleSignInOptions)

        firebaseAuth= FirebaseAuth.getInstance()
        checkUser()

        println("Success")
        binding.googleSignInButton.setOnClickListener{


            val intent=googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)

        }
    }


    private fun checkUser(){
        val firebaseUser=firebaseAuth.currentUser
        println(firebaseUser)
        if(firebaseUser==null){

        }
        else{
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
       // println("\n\nbkjhoijpokjpo;jolkugvfchtdxgrhjv\n\n")
        if(requestCode== RC_SIGN_IN){
            //println("\n\nbkjhoijpokjpo;jolkugvfchtdxgrhjv\n\n")
            val accountTask=GoogleSignIn.getSignedInAccountFromIntent(data)
            println("\n\nbkjhoijpokjpo;jolkugvfchtdxgrhjv\n\n")
            val account=accountTask.getResult(ApiException::class.java)
            println("\n\nbkjhoijpokjpo;jolkugvfchtdxgrhjv\n\n")
            firebaseAuthWithGoogleAccount(account)
        }
    }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?) {

        val credential=GoogleAuthProvider.getCredential(account?.idToken,null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                val firebaseUser=firebaseAuth.currentUser

                val uid=firebaseUser?.uid
                val email=firebaseUser?.email

                if(it.additionalUserInfo?.isNewUser==true){
                    Toast.makeText(this,"Account Created",Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(this,"Logged In",Toast.LENGTH_SHORT).show()
                }
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }.addOnFailureListener{
                Toast.makeText(this,"Login failed due to ${it.message}",Toast.LENGTH_SHORT).show()
            }
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}

