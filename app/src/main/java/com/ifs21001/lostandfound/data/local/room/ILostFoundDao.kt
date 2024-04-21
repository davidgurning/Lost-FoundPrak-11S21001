package com.ifs21001.lostandfound.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ifs21001.lostandfound.data.local.entity.LostFoundEntity

@Dao
interface ILostFoundDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(delcomTodo: LostFoundEntity)

    @Delete
    fun delete(delcomTodo: LostFoundEntity)

    @Query("SELECT * FROM delcom_todos WHERE id = :id LIMIT 1")
    fun get(id: Int): LiveData<LostFoundEntity?>
    @Query("SELECT * FROM delcom_todos ORDER BY created_at DESC")
    fun getAllTodos(): LiveData<List<LostFoundEntity>?>
}