package com.example.bpstore_test.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bpstore_test.R
import com.example.bpstore_test.data.model.LoginRequest
import com.example.bpstore_test.network.retrofit.RetrofitClient
import com.example.bpstore_test.utils.SharedPreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferencesHelper = SharedPreferencesHelper(this)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val idArea = findViewById<EditText>(R.id.idArea)
        val passwordArea = findViewById<EditText>(R.id.passwordArea)

        btnLogin.setOnClickListener {
            val id = idArea.text.toString()
            val password = passwordArea.text.toString()

            if (id.isNotEmpty() && password.isNotEmpty()) {
                loginUser(id, password)
            } else {
                Toast.makeText(this, "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(agentid: String, passwd: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val loginRequest = LoginRequest(agentid, passwd) // JSON 객체 생성
                val response = RetrofitClient.instance.loginUser(loginRequest) // JSON 전송
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        if (user != null) {
                            // 로그인 성공 시 SharedPreferences에 상태 저장
                            sharedPreferencesHelper.saveLoginStatus(true)
                            sharedPreferencesHelper.saveUserId(agentid)

                            Toast.makeText(this@LoginActivity, "로그인 성공!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "로그인 실패: 사용자 정보 없음", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(this@LoginActivity, "로그인 실패: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}