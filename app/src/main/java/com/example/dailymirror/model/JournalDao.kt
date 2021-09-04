package com.example.dailymirror.model

import android.database.Cursor
import android.icu.text.CaseMap
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface JournalDao {

    @Insert
    suspend fun insertEntry(journalModel: JournalModel):Long

    @Query("Select * from JournalModel where date = :dateid")
    suspend fun getEntry(dateid: String): JournalModel

    @Query("Update JournalModel Set image = :byte, title = :title, description = :description, happy = :happy, angry = :angry, surprise = :surprise, sad = :sad, fear = :fear where date = :date")
    suspend fun updateEntry(byte: ByteArray?, title: String, description: String, happy: Int, angry: Int, surprise: Int, sad: Int, fear: Int, date: String)

    @Query("Delete from JournalModel where date = :dateid")
    suspend fun deleteEntry(dateid: String)

    @Query("Select EXISTS (Select * from JournalModel where date = :dateid)")
    suspend fun isPresent(dateid: String):Boolean

    @Query("Select happy as emotion, date from JournalModel where SUBSTR(date,6,2) = :month")
    suspend fun getHappy(month: String): List<BarchartModel>

    @Query("Select angry as emotion, date from JournalModel where SUBSTR(date,6,2) = :month")
    suspend fun getAngry(month: String): List<BarchartModel>

    @Query("Select sad as emotion, date from JournalModel where SUBSTR(date,6,2) = :month")
    suspend fun getSad(month: String): List<BarchartModel>

    @Query("Select surprise as emotion, date from JournalModel where SUBSTR(date,6,2) = :month")
    suspend fun getSurprise(month: String): List<BarchartModel>

    @Query("Select fear as emotion, date from JournalModel where SUBSTR(date,6,2) = :month")
    suspend fun getFear(month: String): List<BarchartModel>
}