package com.popay

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcA
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Header
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import org.json.JSONObject


class NfcReaderActivity : AppCompatActivity()
{

    private val BLOCKS_TO_READ = 0x0F // 16 blocks
    private val NFC_READ_COMMAND = byteArrayOf(
        0x20.toByte(), // Flags
        0x23.toByte(), // Read multiple blocks
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(), // Starting address
        BLOCKS_TO_READ.toByte()  // Number of blocks to be read from the NFC Tag
    )
    private var amount = 50.00;
    private var token = "";

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nfc_reader_activity)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        val bundle = intent.extras
        val sharedPreferences: SharedPreferences = getSharedPreferences("Authentication", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("token", null).toString()
        //amount = bundle?.getDouble("amount")!!
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show()
            finish()
            return
        }
    }


    override fun onNewIntent(intent: Intent) {

        super.onNewIntent(intent)
        val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)

        val nfcA = NfcA.get(tagFromIntent)

        nfcA.connect()


        if (nfcA.isConnected) {
            //val data = nfcA.transceive(NFC_READ_COMMAND)
            nfcA.close()
            val uid = tagFromIntent?.id
            val uidString = uid?.joinToString("") { "%02X".format(it) }

            val queue = Volley.newRequestQueue(this)

            val urlVAlidateCart = "http://10.136.76.77:8080/api/cart/validate"
            val CartValidationRequest : JsonObjectRequest = object : JsonObjectRequest(
                Method.POST, urlVAlidateCart, JSONObject(), { response1 ->
                    if (response1.getBoolean("success")) {
                        Toast.makeText(this, "Payment successful", Toast.LENGTH_LONG).show()
                        val intent1 = Intent(this, MainActivity::class.java)
                        startActivity(intent1)
                        finish()
                    } else {
                        val intent1 = Intent(this, MainActivity::class.java)
                        startActivity(intent1)
                        Toast.makeText(this, "Payment failed", Toast.LENGTH_LONG).show()
                    }
                }, {
                    val intent1 = Intent(this, MainActivity::class.java)
                    startActivity(intent1)
                    Toast.makeText(this, "transaction failed - insufficient funds", Toast.LENGTH_LONG).show()
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val params2: MutableMap<String, String> = HashMap()
                    params2["Authorization"] = "Bearer $token"
                    return params2
                }
            }

            val creditHistoryurl = "http://10.136.76.77:8080/api/credit_history/$uidString"
            val params = HashMap<String, Double>()
            params["amount"] = amount
            val creditRequest = object : JsonObjectRequest(
                Method.POST, creditHistoryurl, JSONObject((params as Map<*, *>?)!!),
                { response ->
                    if (response.getBoolean("success")) {
                        queue.add(CartValidationRequest)
                    } else {
                        Toast.makeText(this, "Payment failed", Toast.LENGTH_LONG).show()
                    }
                },
                { error ->
                    val intent1 = Intent(this, MainActivity::class.java)
                    startActivity(intent1)
                    Toast.makeText(this, "Payment failed, Invalid Credit Card", Toast.LENGTH_LONG).show()
                    Log.d("Error.Response", error.toString())
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val params1: MutableMap<String, String> = HashMap()
                    params1["Authorization"] = "Bearer $token"
                    return params1
                }
            }
            queue.add(creditRequest)
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        300,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            }
        } else {
            Log.e("ans", "Not connected")
        }
    }

    private fun enableForegroundDispatched(activity: AppCompatActivity, adapter: NfcAdapter) {
        val intent = Intent(this, this.javaClass)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(activity.applicationContext, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(activity.applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }
        val filters = arrayOfNulls<IntentFilter>(1)
        val techList = arrayOf<Array<String>>()
        filters[0] = IntentFilter()
        with(filters[0]) {
            this?.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED)
            this?.addCategory(Intent.CATEGORY_DEFAULT)
            try {
                this?.addDataType("text/plain")
            } catch (ex: IntentFilter.MalformedMimeTypeException) {
                throw RuntimeException(ex)
            }
        }
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList)
    }
    override fun onResume() {
        super.onResume()
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        enableForegroundDispatched(this, nfcAdapter!!)
    }
}