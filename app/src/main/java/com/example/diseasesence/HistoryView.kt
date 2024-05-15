package com.example.diseasesence

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diseasesence.databinding.ActivityHistoryViewBinding
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.util.Date

class HistoryView : AppCompatActivity() {
    private lateinit var binding:ActivityHistoryViewBinding
    private lateinit var hisadapter:RecylerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_history_view)

        binding.backstackpop.setOnClickListener {
            var i=Intent(this,ScanActivity::class.java)
            startActivity(i)
        }
        var db=Firebase.firestore
        val collection=db.collection("Users")

        collection.whereEqualTo("userId", Users.instence?.userId.toString())
            .get().addOnSuccessListener {querysnapshot->
                if (querysnapshot!=null){
                    var Foods= mutableListOf<ItemModel>()
                    for (snapshot in querysnapshot){
                        Foods.add(
                            ItemModel(snapshot.data.get("userId").toString(),
                            snapshot.data.get("name").toString(),
                                snapshot.data.get("image_name").toString(),
                            snapshot.data.get("time") as Timestamp,
                            snapshot.data.get("image_Url").toString()
                        )
                        )
                        hisadapter=RecylerAdapter(Foods)
                        binding.historyitems.apply {
                            this.layoutManager=LinearLayoutManager(context)
                            setHasFixedSize(true)
                            adapter=hisadapter
                    }
                }

            }


        }
    }
}