package com.popay

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity



@SuppressLint("CustomSplashScreen")
@Suppress("DEPRECATION")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPreferences: SharedPreferences = getSharedPreferences("Authentication", Context.MODE_PRIVATE)
            sharedPreferences.getString("token", null)?.let {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } ?: run {
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, 3000)
    }
}
