package com.example.bpstore_test.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.bpstore_test.R
import com.example.bpstore_test.ui.fragment.FirstFragment

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // registeractivity activity
        Handler().postDelayed({
            startActivity(Intent(this,   LoginActivity::class.java))
            finish()
        },2000)

        // main activity
//        Handler().postDelayed({
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        },2000)

    }
}