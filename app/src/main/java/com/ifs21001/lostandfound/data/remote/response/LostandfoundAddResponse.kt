package com.ifs21001.lostandfound.data.remote.response

import com.google.gson.annotations.SerializedName

data class LostandfoundAddResponse(

	@field:SerializedName("data")
	val data: DataAddLostandFound,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class DataAddLostandFound(

	@field:SerializedName("lost_found_id")
	val lostFoundId: Int
)
