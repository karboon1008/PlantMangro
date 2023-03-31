package com.example.recycleviewwithclicklistener

import java.sql.Blob
import java.util.*

data class MangroveModel(
    var id:Int= getAutoId(),
    var name: String ="",
    var image: ByteArray
){
    companion object{
        fun getAutoId():Int {
            val random = Random()
            return random.nextInt(100)
        }
    }
}