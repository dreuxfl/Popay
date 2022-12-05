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

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nfc_reader_activity)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show()
            finish()
            return
        }

    }


    override fun onNewIntent(intent: Intent?)
    {
        super.onNewIntent(intent)
        Toast.makeText(this, "NEW INTENT", Toast.LENGTH_LONG).show()
        var tagFromIntent: Tag? = intent?.getParcelableExtra(NfcAdapter.EXTRA_TAG)

        val nfc = NfcA.get(tagFromIntent)

        val atqa: ByteArray = nfc.atqa
        val sak: Short = nfc.sak
        nfc.connect()

        if(nfc.isConnected)
        {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        300,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            }
                val receivedData:ByteArray= nfc.transceive(NFC_READ_COMMAND)
            Toast.makeText(this@NfcReaderActivity, "NFC READ", Toast.LENGTH_SHORT).show()

        } else{
    Log.e("ans", "Not connected")
            }
    }

    private fun enableForegroundDispatch(activity: AppCompatActivity, adapter: NfcAdapter?) {

        val intent = Intent(activity.applicationContext, activity.javaClass)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(activity.applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val filters = arrayOfNulls<IntentFilter>(1)
        val techList = arrayOf<Array<String>>()
        filters[0] = IntentFilter()
        with(filters[0]) {
            this?.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED)
            this?.addCategory(Intent.CATEGORY_DEFAULT)
            try {
                this?.addDataType("text/plain")
            } catch (ex: IntentFilter.MalformedMimeTypeException) {
                throw RuntimeException("err")
            }
        }
        adapter?.enableForegroundDispatch(activity, pendingIntent, filters, techList)

    }
    override fun onResume() {
        super.onResume()
        enableForegroundDispatch(this, this.nfcAdapter)

    }
}