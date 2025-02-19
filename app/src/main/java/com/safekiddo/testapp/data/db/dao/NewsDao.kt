package com.safekiddo.testapp.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.safekiddo.testapp.data.db.entity.News
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface NewsDao : BaseDao<News> {

    @Query("SELECT * FROM news ORDER BY modification_date DESC, id ASC")
    fun observeAllRx(): Observable<List<News>>

    @Query("SELECT * FROM news WHERE id = :newsId")
    fun getByIdRx(newsId: Long): Single<News>

    @Query("DELETE FROM news WHERE id = :newsId")
    fun deleteById(newsId: Long): Completable
}