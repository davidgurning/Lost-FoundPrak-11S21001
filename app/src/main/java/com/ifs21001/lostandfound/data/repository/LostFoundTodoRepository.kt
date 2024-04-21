package com.ifs21001.lostandfound.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.ifs21001.lostandfound.data.local.entity.LostFoundEntity
import com.ifs21001.lostandfound.data.local.room.ILostFoundDao
import com.ifs21001.lostandfound.data.local.room.LostFoundDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class LostFoundTodoRepository(context: Context) {
    private val mDelcomTodoDao: ILostFoundDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    init {
        val db = LostFoundDatabase.getInstance(context)
        mDelcomTodoDao = db.delcomTodoDao()
    }
    fun getAllTodos(): LiveData<List<LostFoundEntity>?> = mDelcomTodoDao.getAllTodos()

    fun get(todoId: Int): LiveData<LostFoundEntity?> = mDelcomTodoDao.get(todoId)

    fun insert(todo: LostFoundEntity) {
        executorService.execute { mDelcomTodoDao.insert(todo) }
    }

    fun delete(todo: LostFoundEntity) {
        executorService.execute { mDelcomTodoDao.delete(todo) }
    }

    companion object {
        @Volatile
        private var INSTANCE: LostFoundTodoRepository? = null
        fun getInstance(
            context: Context
        ): LostFoundTodoRepository {
            synchronized(LostFoundTodoRepository::class.java) {
                INSTANCE = LostFoundTodoRepository(
                    context
                )
            }
            return INSTANCE as LostFoundTodoRepository
        }
    }

}