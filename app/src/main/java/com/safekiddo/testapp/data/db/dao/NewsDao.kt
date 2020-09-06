package com.safekiddo.testapp.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.safekiddo.testapp.data.db.entity.News
import io.reactivex.Single

@Dao
interface NewsDao : BaseDao<News> {

    @Query("SELECT * FROM news")
    fun getAllRx(): Single<List<News>>
}