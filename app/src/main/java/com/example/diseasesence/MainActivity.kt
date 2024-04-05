package com.example.diseasesence

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.diseasesence.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    private lateinit var auth:FirebaseAuth
    var TAG="TAGY"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)
        auth=Firebase.auth

        binding.Loginbtn.setOnClickListener {
            var email:String=binding.username.text.toString().trim()
            var password:String=binding.password.text.toString().trim()

            if (email!=""||password!=""){
                insertUser(email,password)
            }
        }

        binding.singupbtn.setOnClickListener {
            var intent=Intent(this,signUp::class.java)
            startActivity(intent)
        }

    }

    private fun insertUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    Users.instence?.userId = user?.uid
                    var i=Intent(this,ScanActivity::class.java)
                    startActivity(i)



                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    //updateUI(null)
                }
            }

    }


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Users.instence?.userId = currentUser.uid
            reload()
        }
    }

    private fun reload() {
        var i=Intent(this,ScanActivity::class.java)
        startActivity(i)
    }
}