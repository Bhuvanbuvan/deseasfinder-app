package com.example.diseasesence

import android.app.Application

class Users :Application(){
    var userId:String?=null

    companion object{
        var instence:Users?=null
            get() {
                if (field == null) {
                    field= Users()
                }
                return field
            }
            private set
    }
}