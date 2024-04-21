package com.ifs21001.lostandfound.helper

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.ifs21001.lostandfound.data.local.entity.LostFoundEntity
import com.ifs21001.lostandfound.data.remote.MyResult
import com.ifs21001.lostandfound.data.remote.response.LostFoundsItem

class Utils {
    companion object {
        fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
            val observerWrapper = object : Observer<T> {
                override fun onChanged(value: T) {
                    observer(value)
                    if (value is MyResult.Success<*> ||
                        value is MyResult.Error
                    ) {
                        removeObserver(this)
                    }
                }
            }
            observeForever(observerWrapper)
        }
        fun entitiesToResponses(entities: List<LostFoundEntity>):
                List<LostFoundsItem> {
            val responses = ArrayList<LostFoundsItem>()
            entities.map {
                val response = LostFoundsItem(
                    cover = it.cover,
                    updatedAt = it.updatedAt,
                    userId = it.user_id,
                    description = it.description,
                    createdAt = it.createdAt,
                    id = it.id,
                    title = it.title,
                    isCompleted = it.isCompleted,
                    status = it.status,
                )
                responses.add(response)
            }
            return responses
        }
    }
}