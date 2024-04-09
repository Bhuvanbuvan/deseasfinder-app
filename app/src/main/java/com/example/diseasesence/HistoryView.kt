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
import com.google.firebase.Timestamp
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
        var time=Timestamp(Date())
        var items= arrayListOf(
            ItemModel(Users.instence?.userId.toString(),"grapes","imagename", time,"https://firebasestorage.googleapis.com/v0/b/disease-sence.appspot.com/o/CDRsOj7HdsaXFv0H8VQ1R7a6yMs1%2F08f7b3c7-6a9b-4339-ad0d-a2ae9b57cb2f.jpg?alt=media&token=37762a43-3b61-4843-b5b6-d049193edc68"),
            ItemModel(Users.instence?.userId.toString(),"grapes","imagename", time,"https://firebasestorage.googleapis.com/v0/b/disease-sence.appspot.com/o/CDRsOj7HdsaXFv0H8VQ1R7a6yMs1%2F08f7b3c7-6a9b-4339-ad0d-a2ae9b57cb2f.jpg?alt=media&token=37762a43-3b61-4843-b5b6-d049193edc68"),
            ItemModel(Users.instence?.userId.toString(),"grapes","imagename", time,"https://firebasestorage.googleapis.com/v0/b/disease-sence.appspot.com/o/CDRsOj7HdsaXFv0H8VQ1R7a6yMs1%2F08f7b3c7-6a9b-4339-ad0d-a2ae9b57cb2f.jpg?alt=media&token=37762a43-3b61-4843-b5b6-d049193edc68"),
            ItemModel(Users.instence?.userId.toString(),"grapes","imagename", time,"https://firebasestorage.googleapis.com/v0/b/disease-sence.appspot.com/o/CDRsOj7HdsaXFv0H8VQ1R7a6yMs1%2F08f7b3c7-6a9b-4339-ad0d-a2ae9b57cb2f.jpg?alt=media&token=37762a43-3b61-4843-b5b6-d049193edc68")
        )

        hisadapter=RecylerAdapter(items)
        binding.historyitems.apply {
            this.layoutManager=LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter=hisadapter
        }
    }
}