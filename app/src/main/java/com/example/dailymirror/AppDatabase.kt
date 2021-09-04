package com.example.dailymirror

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dailymirror.model.JournalDao
import com.example.dailymirror.model.JournalModel
import com.example.dailymirror.model.TodoDao
import com.example.dailymirror.model.TodoModel
import com.example.dailymirror.util.Constants

@Database(entities = [TodoModel::class, JournalModel::class], version = 13)
abstract class AppDatabase: RoomDatabase(){

    abstract fun todoDao(): TodoDao
    abstract fun journalDao(): JournalDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase?=null

        fun getDatabase(context: Context):AppDatabase{
            val tempInstance = INSTANCE
            if(tempInstance!=null)
                return tempInstance

            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    Constants.DB_NAME
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}