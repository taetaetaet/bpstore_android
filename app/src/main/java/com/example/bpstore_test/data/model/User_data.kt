package com.example.bpstore_test.data.model

data class User_data(
    val agentid: String, // AGENTID
    val passwd: String,  // PASSWD
    val name: String? = null, // 고객명 (nullable)
    val b_num: String? = null, // 사업자번호 (nullable)
    val c_num: String? = null, // 청구계정번호 (nullable)
    val tel: String? = null,
    val addr: String? = null,
    val detail_addr: String? = null,
    val r_num: String? = null,
    val etc: String? = null

)