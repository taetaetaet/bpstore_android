package com.example.bpstore_test.network.api

import com.example.bpstore_test.data.model.LoginRequest
import com.example.bpstore_test.data.model.UserBusinessRequest
import com.example.bpstore_test.data.model.User_data
import com.example.bpstore_test.network.retrofit.RetrofitClient
import com.example.bpstore_test.ui.adapter.HistoryAdapter
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @POST("/api/users/login")
    suspend fun loginUser(
        @Body loginRequest: LoginRequest // JSON 형식으로 전송
    ): Response<User_data>

    @POST("/api/users/register")
    suspend fun registerUser(
        @Body loginRequest: LoginRequest // JSON 형식으로 전송
    ): Response<User_data>

    @POST("/api/users/saveBusiness")
    suspend fun saveUserBusiness(
        @Body userBusinessRequest: UserBusinessRequest // JSON 형식으로 전송
    ): Response<User_data>

    @GET("/api/users/business/{agentId}")
    suspend fun getUserBusinessByAgentId(@Path("agentId") agentId: String): Response<List<User_data>>{
        val response = RetrofitClient.instance.getUserBusinessByAgentId(agentId)
        return response
    }


}