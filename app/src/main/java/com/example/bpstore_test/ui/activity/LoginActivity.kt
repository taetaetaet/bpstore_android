package com.example.bpstore_test.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bpstore_test.R
import com.example.bpstore_test.model.User_data
import com.example.bpstore_test.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val idArea = findViewById<EditText>(R.id.idArea)
        val passwordArea = findViewById<EditText>(R.id.passwordArea)

        btnLogin.setOnClickListener {
            val id = idArea.text.toString()
            val password = passwordArea.text.toString()

            if(id.isNotEmpty() && password.isNotEmpty()){
                loginUser(id, password)
            } else{
                Toast.makeText(this, "있는거 다 쳐라", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(agentid: String, passwd: String) {
        val user = User_data(agentid, passwd)

        CoroutineScope(Dispatchers.IO).launch{
            try {
                val response = RetrofitClient.apiService.loginUser(user)
                withContext(Dispatchers.Main) {
                    if(response.isSuccessful){
                        Toast.makeText(this@LoginActivity, "해윙해윙해윙", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "안돼 돌아가", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@LoginActivity, "어어 네트웤 공습경보!! ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}