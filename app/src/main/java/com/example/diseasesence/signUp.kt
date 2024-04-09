package com.example.diseasesence

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.diseasesence.databinding.ActivityMainBinding
import com.example.diseasesence.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class signUp : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    var TAG="TAGY"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_sign_up)
        auth= Firebase.auth

        binding.signup.setOnClickListener {
            var email:String=binding.username.text.toString().trim()
            var password:String=binding.password.text.toString().trim()

            if (email!=""||password!=""){
                insertUser(email,password)
            }
        }

        binding.loginbtn.setOnClickListener {
            var i =Intent(this,MainActivity::class.java)
            startActivity(i)
        }


    }

    private fun insertUser(email:String,password:String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    //updateUI(null)
                    Users.instence?.userId = user?.uid
                    var i=Intent(this,ScanActivity::class.java)
                    startActivity(i)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    // updateUI(null)
                }
            }
    }


}