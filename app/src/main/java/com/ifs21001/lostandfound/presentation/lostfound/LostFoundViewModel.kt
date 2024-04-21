package com.ifs21001.lostandfound.presentation.lostfound

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ifs21001.lostandfound.data.local.entity.LostFoundEntity
import com.ifs21001.lostandfound.data.remote.MyResult
import com.ifs21001.lostandfound.data.remote.response.DataAddLostandFound
import com.ifs21001.lostandfound.data.remote.response.LostandfoundAppResponse
import com.ifs21001.lostandfound.data.remote.response.LostfoundResponse
import com.ifs21001.lostandfound.data.repository.LostFoundRepository
import com.ifs21001.lostandfound.data.repository.LostFoundTodoRepository
import com.ifs21001.lostandfound.presentation.ViewModelFactory

class LostFoundViewModel(
    private val lostFoundRepository: LostFoundRepository,
    private val localTodoRepository: LostFoundTodoRepository
) : ViewModel() {
    fun getLostFound(lostFoundId: Int): LiveData<MyResult<LostfoundResponse>> {
        return lostFoundRepository.getLostFound(lostFoundId).asLiveData()
    }
    fun postlostFound(
        title: String,
        description: String,
        status: String,
    ): LiveData<MyResult<DataAddLostandFound>> {
        return lostFoundRepository.postlostFound(
            title,
            description,
            status
        ).asLiveData()
    }
    fun putlostFound(
        todoId: Int,
        title: String,
        description: String,
        status: String,
        isCompleted: Boolean,
    ): LiveData<MyResult<LostandfoundAppResponse>> {
        return lostFoundRepository.putLostFound(
            todoId,
            title,
            description,
            status,
            isCompleted,
        ).asLiveData()
    }
    fun deletelostFound(lostFoundId: Int): LiveData<MyResult<LostandfoundAppResponse>> {
        return lostFoundRepository.deletelostFound(lostFoundId).asLiveData()
    }
    fun getLocalTodos(): LiveData<List<LostFoundEntity>?> {
        return localTodoRepository.getAllTodos()
    }

    fun getLocalTodo(todoId: Int): LiveData<LostFoundEntity?> {
        return localTodoRepository.get(todoId)
    }


    fun insertLocalTodo(todo: LostFoundEntity) {
        localTodoRepository.insert(todo)
    }

    fun deleteLocalTodo(todo: LostFoundEntity) {
        localTodoRepository.delete(todo)
    }

    companion object {
        @Volatile
        private var INSTANCE: LostFoundViewModel? = null
        fun getInstance(
            todoRepository: LostFoundRepository,
            localTodoRepository: LostFoundTodoRepository,
        ): LostFoundViewModel {
            synchronized(ViewModelFactory::class.java) {
                INSTANCE = LostFoundViewModel(
                    todoRepository,
                    localTodoRepository
                )
            }
            return INSTANCE as LostFoundViewModel
        }
    }
}