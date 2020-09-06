package com.safekiddo.testapp.data.db.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import io.reactivex.Completable

interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: List<T>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRx(data: T): Completable

    @Update
    fun updateRx(data: T): Completable

    @Delete
    fun delete(data: T)
}