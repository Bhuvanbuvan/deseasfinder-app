package com.example.diseasesence

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.diseasesence.databinding.ActivityHistoryViewBinding
import com.example.diseasesence.databinding.HistoryItemBinding
import com.squareup.picasso.Picasso

class RecylerAdapter(private var items:List<ItemModel>):RecyclerView.Adapter<RecylerAdapter.ViewHolder>() {

    class ViewHolder(var historyItemBinding: HistoryItemBinding):RecyclerView.ViewHolder(historyItemBinding.root){
        fun historyitem(item:ItemModel){
            historyItemBinding.datetime.text="Date: "+item.time.toDate().toString()
            Picasso.get().load(item.image_Url).into(historyItemBinding.image)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var layoutInflater=LayoutInflater.from(parent.context)
        var bind:HistoryItemBinding =HistoryItemBinding.inflate(layoutInflater,
            parent,
            false)
        return ViewHolder(bind)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.historyitem(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}