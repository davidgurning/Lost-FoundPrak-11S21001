package com.ifs21001.lostandfound.data.remote.retrofit

import com.ifs21001.lostandfound.data.remote.response.AuthorLostFounds
import com.ifs21001.lostandfound.data.remote.response.DelcomLoginResponse
import com.ifs21001.lostandfound.data.remote.response.DelcomResponse
import com.ifs21001.lostandfound.data.remote.response.DelcomUserResponse
import com.ifs21001.lostandfound.data.remote.response.LostfoundResponse
import com.ifs21001.lostandfound.data.remote.response.LostfoundsResponse
import com.ifs21001.lostandfound.data.remote.response.LostandfoundAddResponse
import com.ifs21001.lostandfound.data.remote.response.LostandfoundAppResponse
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
interface IApiService {
    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): DelcomResponse
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): DelcomLoginResponse
    @GET("users/me")
    suspend fun getMe(): DelcomUserResponse
    @FormUrlEncoded
    @POST("lost-founds")
    suspend fun postLostFound(
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("status") status: String,
    ): LostandfoundAddResponse
    @FormUrlEncoded
    @PUT("lost-founds/{id}")
    suspend fun putLostFound(
        @Path("id") lostFoundId: Int,
        @Field("title") title: String,
        @Field("status") status: String,
        @Field("description") description: String,
        @Field("is_completed") isCompleted: Int
    ): LostandfoundAppResponse
    @GET("lost-founds")
    suspend fun getLostFounds(
        @Query("is_completed") isCompleted: Int?
    ): LostfoundsResponse
    @GET("lost-founds/{id}")
    suspend fun getLostFound(
        @Path("id") lostFoundId: Int,
    ):  LostfoundResponse
    @DELETE("lost-founds/{id}")
    suspend fun deleteLostFound(
        @Path("id") lostFoundId: Int,
    ): LostandfoundAppResponse
}