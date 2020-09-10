package com.safekiddo.testapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.safekiddo.testapp.data.db.converter.ImageSourceTypeConverter
import com.safekiddo.testapp.data.db.dao.NewsDao
import com.safekiddo.testapp.data.db.entity.News

@Database(
        entities = [
            News::class
        ],
        version = 1,
        exportSchema = false
)
@TypeConverters(ImageSourceTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract val newsDao: NewsDao

    companion object {

        fun build(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }
}