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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val idArea = findViewById<EditText>(R.id.etUsername)
        val passwordArea = findViewById<EditText>(R.id.etPassword)

        btnRegister.setOnClickListener {
            val agentid = idArea.text.toString()
            val passwd = passwordArea.text.toString()

            if (agentid.isNotEmpty() && passwd.isNotEmpty()) {
                registerUser(agentid, passwd)
            } else {
                Toast.makeText(this, "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(agentid: String, passwd: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // LoginRequest 객체 생성
                val loginRequest = LoginRequest(agentid, passwd)

                // Retrofit을 통해 서버에 요청
                val response = RetrofitClient.instance.registerUser(loginRequest)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        if (user != null) {
                            Toast.makeText(this@RegisterActivity, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@RegisterActivity, "회원가입 실패: 서버 오류", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(this@RegisterActivity, "회원가입 실패: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}