package com.example.diseasesence

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.diseasesence.databinding.ActivityHistoryViewBinding
import com.google.firebase.Timestamp
import java.util.Date

class HistoryView : AppCompatActivity() {
    private lateinit var binding:ActivityHistoryViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_history_view)

        var items= arrayListOf(
            ItemModel(Users.instence?.userId.toString(),"imagename", Timestamp(Date()),"http://d:docl.img"),
            ItemModel(Users.instence?.userId.toString(),"imagename", Timestamp(Date()),"http://d:docl.img"),
            ItemModel(Users.instence?.userId.toString(),"imagename", Timestamp(Date()),"http://d:docl.img"),
            ItemModel(Users.instence?.userId.toString(),"imagename", Timestamp(Date()),"http://d:docl.img")
        )

    }
}