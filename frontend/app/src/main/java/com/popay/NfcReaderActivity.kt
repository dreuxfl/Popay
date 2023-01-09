package com.popay

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
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
    private var amount = 0.00;

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nfc_reader_activity)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        val bundle = intent.extras
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
            print("UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU")
            Log.d("tag ID", uidString.toString())
          //  val queue = Volley.newRequestQueue(this)
            //  val url = "http://localhost:8080/api/credit_history/$uidString"
            //   val params = HashMap<String, Double>()
            //   params["amount"] = amount
            //   val stringRequest = JsonObjectRequest(
            //      Request.Method.POST, url, JSONObject((params as Map<*, *>?)!!),
            //      { response ->
            //          Log.d("Response", response.toString())
            //     },
        //      { error ->
            //          Log.d("Error.Response", error.toString())
            //       }
            //    )
            //    queue.add(stringRequest)
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