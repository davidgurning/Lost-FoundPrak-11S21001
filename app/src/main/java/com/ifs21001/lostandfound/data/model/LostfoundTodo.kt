package com.ifs21001.lostandfound.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LostfoundTodo(
    val id: Int,
    val userId: Int,
    val title: String,
    val description: String,
    val status: String,
    var isCompleted: Boolean,
    val cover: String?,
) : Parcelable