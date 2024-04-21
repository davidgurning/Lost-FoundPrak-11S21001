package com.ifs21001.lostandfound.data.remote.response

import com.google.gson.annotations.SerializedName

data class LostandfoundAppResponse(

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)
