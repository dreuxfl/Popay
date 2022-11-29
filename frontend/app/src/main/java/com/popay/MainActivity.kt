package com.popay

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

import com.popay.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val btnScan = findViewById<Button>(R.id.scanBtn)
        btnScan.setOnClickListener {
            val intent = Intent(this, QRCodeScannerActivity::class.java)
            startActivity(intent)
        }

        val btnNfc = findViewById<Button>(R.id.btnNfc)
        btnNfc.setOnClickListener {
            val intent = Intent(this, NfcReaderActivity::class.java)
            startActivity(intent)
        }

    }
}