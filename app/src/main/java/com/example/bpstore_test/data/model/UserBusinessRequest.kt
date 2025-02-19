package com.example.bpstore_test.data.model

data class UserBusinessRequest(
    val agentid: String,
    val name: String,
    val b_num: String,
    val c_num: String,
    val tel: String,
    val addr: String,
    val detail_addr: String,
    val r_num: String,
    val etc: String,
    val bz_type: Int
)