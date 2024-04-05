package com.example.diseasesence

import com.google.firebase.Timestamp

data class ItemModel(
    val userId:String,
    val image_name:String,
    val time:Timestamp,
    val image_Url:String
)
