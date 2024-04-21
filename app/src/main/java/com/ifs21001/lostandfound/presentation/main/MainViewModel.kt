package com.ifs21001.lostandfound.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ifs21001.lostandfound.data.pref.UserModel
import com.ifs21001.lostandfound.data.remote.MyResult
import com.ifs21001.lostandfound.data.remote.response.LostandfoundAppResponse
import com.ifs21001.lostandfound.data.remote.response.LostfoundsResponse
import com.ifs21001.lostandfound.data.repository.AuthRepository
import com.ifs21001.lostandfound.data.repository.LostFoundRepository
import com.ifs21001.lostandfound.presentation.ViewModelFactory
import kotlinx.coroutines.launch
class MainViewModel(
    private val authRepository: AuthRepository,
    private val lostfoundRepository: LostFoundRepository
) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return authRepository.getSession().asLiveData()
    }
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
    fun getTodos(): LiveData<MyResult<LostfoundsResponse>> {
        return lostfoundRepository.getLostFounds(null).asLiveData()
    }
    fun putTodo(
        todoId: Int,
        title: String,
        description: String,
        status: String,
        isCompleted: Boolean,
    ): LiveData<MyResult<LostandfoundAppResponse>> {
        return lostfoundRepository.putLostFound(
            todoId,
            title,
            description,
            status,
            isCompleted,
        ).asLiveData()
    }
    companion object {
        @Volatile
        private var INSTANCE: MainViewModel? = null
        fun getInstance(
            authRepository: AuthRepository,
            lostfoundRepository: LostFoundRepository
        ): MainViewModel {
            synchronized(ViewModelFactory::class.java) {
                INSTANCE = MainViewModel(
                    authRepository,
                    lostfoundRepository
                )
            }
            return INSTANCE as MainViewModel
        }
    }
}
