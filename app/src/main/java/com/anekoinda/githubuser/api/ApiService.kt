package com.anekoinda.githubuser.api

import com.anekoinda.githubuser.BuildConfig
import com.anekoinda.githubuser.model.User
import com.anekoinda.githubuser.model.UserDetailResponse
import com.anekoinda.githubuser.model.UserResponse
import retrofit2.Call
import retrofit2.http.*

const val apiKey = BuildConfig.apiKey

interface ApiService {
    @GET("search/users")
    @Headers("Authorization: token $apiKey")
    fun getUser(
        @Query("q") query: String
    ): Call<UserResponse>

    @GET("users/{username}")
    @Headers("Authorization: token $apiKey")
    fun getUserDetail(
        @Path("username") username: String
    ): Call<UserDetailResponse>

    @GET("users/{username}/followers")
    @Headers("Authorization: token $apiKey")
    fun getFollowers(
        @Path("username") username: String
    ): Call<ArrayList<User>>

    @GET("users/{username}/following")
    @Headers("Authorization: token $apiKey")
    fun getFollowing(
        @Path("username") username: String
    ): Call<ArrayList<User>>
}