package com.popay

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.popay.databinding.QrcodeScannerBinding
import com.popay.entities.Product
import java.io.IOException



class QRCodeScannerActivity : AppCompatActivity() {

    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private var scannedValue = ""
    private var baseUrl : String? = null
    private lateinit var binding: QrcodeScannerBinding
    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = QrcodeScannerBinding.inflate(layoutInflater)
        val view = binding.root
        baseUrl = this.getString(R.string.baseUrl)
        setContentView(view)
        val sharedPreferences: SharedPreferences = getSharedPreferences("Authentication", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("token", null).toString()

        if (ContextCompat.checkSelfPermission(
                this@QRCodeScannerActivity, android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForCameraPermission()
        } else {
            setupControls()
        }


        val aniSlide: Animation =
            AnimationUtils.loadAnimation(this@QRCodeScannerActivity, R.anim.scanner_animation)
        binding.barcodeLine.startAnimation(aniSlide)
    }


    private fun setupControls() {
        barcodeDetector =
            BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()

        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true)
            .build()

        binding.cameraSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {

            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this@QRCodeScannerActivity,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(this@QRCodeScannerActivity, "You need the give the necessary permissions", Toast.LENGTH_SHORT).show()
                        return
                    }
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }


            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this@QRCodeScannerActivity,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(this@QRCodeScannerActivity, "You need the give the necessary permissions", Toast.LENGTH_SHORT).show()
                        return
                    }
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })


        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Toast.makeText(applicationContext, "Scanner has been closed", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun receiveDetections(detections: Detections<Barcode>) {

                val barcodes = detections.detectedItems
                if (barcodes.size() == 1) {
                    scannedValue = barcodes.valueAt(0).rawValue

                    runOnUiThread {
                        cameraSource.stop()

                        val queue = Volley.newRequestQueue(this@QRCodeScannerActivity)
                        val jsonObjectRequest : JsonObjectRequest = object : JsonObjectRequest(
                            "$baseUrl/product/$scannedValue",
                            null,
                            { response ->
                                val product = Product(
                                    response.getString("id").toInt(),
                                    response.getString("caption"),
                                    response.getString("price").toDouble(),
                                    response.getString("description")
                                )
                                val intent = Intent(this@QRCodeScannerActivity, ProductPopup::class.java)
                                intent.putExtra("product", product)
                                startActivity(intent)
                            },
                            { error ->
                                println("Err $error")
                            }
                        ){
                            override fun getHeaders(): MutableMap<String, String> {
                                val params2: MutableMap<String, String> = HashMap()
                                params2["Authorization"] = "Bearer $token"
                                return params2
                            }
                        }
                        queue.add(jsonObjectRequest)
                        finish()
                    }
                }else
                {
                    Toast.makeText(this@QRCodeScannerActivity, "value- else", Toast.LENGTH_SHORT).show()

                }
            }
        })
    }

    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            this@QRCodeScannerActivity,
            arrayOf(android.Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupControls()
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource.stop()
    }
}

