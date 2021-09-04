package com.example.dailymirror.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.Nullable

@Entity
data class JournalModel(

    var image: ByteArray?,
    var title:String,
    var description:String,
    var happy:Int,
    var angry:Int,
    var surprise:Int,
    var sad:Int,
    var fear:Int,
    @PrimaryKey
    var date: String
//        var isFinished: Int = 0,
//        @PrimaryKey(autoGenerate = true)
//        var id:Long = 0
)