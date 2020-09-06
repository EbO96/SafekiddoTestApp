package com.safekiddo.testapp.data.db.dao

import androidx.room.Dao
import com.safekiddo.testapp.data.db.entity.Post

@Dao
interface PostDao : BaseDao<Post>