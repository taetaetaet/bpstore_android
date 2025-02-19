package com.example.bpstore_test.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.bpstore_test.R
import com.example.bpstore_test.utils.SharedPreferencesHelper

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferencesHelper = SharedPreferencesHelper(this)

        val btnLogout = findViewById<ImageView>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            // 로그아웃 처리
            sharedPreferencesHelper.logout()

            // LoginActivity로 이동
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}