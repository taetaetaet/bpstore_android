package com.example.bpstore_test.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class User_data(
    val agentid: String, // AGENTID
    val name: String? = null, // 고객명 (nullable)
    val b_num: String? = null, // 사업자번호 (nullable)
    val c_num: String? = null, // 청구계정번호 (nullable)
    val tel: String? = null,
    val addr: String? = null,
    val detail_addr: String? = null,
    val r_num: String? = null,
    val etc: String? = null,
    val bz_type: Int?,
    val passwd: String = "", // 비밀번호 (기본값 "")
    val created_at: String? = null
) {
    fun getCreatedAtDate() : Date? {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        return try {
            if (!created_at.isNullOrEmpty()) {
                format.parse(created_at)
            } else {
                null
            }
        } catch (e: Exception){
            null
        }
    }
}
