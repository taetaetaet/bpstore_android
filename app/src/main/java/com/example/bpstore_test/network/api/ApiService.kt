package com.example.bpstore_test.network.api

import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import com.example.bpstore_test.R
import com.example.bpstore_test.data.model.User_data
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("/api/users/login")
    suspend fun loginUser(
        @Query("agentid") agentid: String,
        @Query("passwd") passwd: String
    ): Response<User_data>

    @POST("/api/users/register")
    suspend fun registerUser(
        @Query("agentid") agentid: String,
        @Query("passwd") passwd: String
    ): Response<User_data>

    @POST("/api/users/saveBusiness")
    suspend fun saveUserBusiness(
        @Query("agentid") agentid: String,
        @Query("name") name: String,
        @Query("b_num") b_num: String,
        @Query("c_num") c_num: String,
        @Query("tel") tel: String,
        @Query("addr") addr: String,
        @Query("detail_addr") detail_addr: String,
        @Query("r_num") r_num: String,
        @Query("etc") etc: String,
        @Query("bz_type") bz_type: Int
    ): Response<User_data>
}



