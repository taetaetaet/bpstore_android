package com.example.bpstore_test.api

import com.example.bpstore_test.model.User_data
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/api/users/register") // 회원가입용 테스트 끝날시 *삭제 필요*
    suspend fun registerUser(@Body user: User_data): Response<User_data>

    @POST("/api/users/login") // 로그인용
    suspend fun loginUser(@Body user: User_data): Response<User_data>
}