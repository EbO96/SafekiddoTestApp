package com.safekiddo.testapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.safekiddo.testapp.data.db.dao.PostDao
import com.safekiddo.testapp.data.db.entity.Post

@Database(
        entities = [
            Post::class
        ],
        version = 1,
        exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getPostDao(): PostDao

    companion object {

        fun build(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }
}