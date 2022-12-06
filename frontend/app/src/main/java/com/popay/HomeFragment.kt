package com.popay

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        view.findViewById<Button>(R.id.scan_btn).setOnClickListener { _ ->
            val intent = Intent (activity, QRCodeScannerActivity::class.java)
            activity?.startActivity(intent)
        }

        view.findViewById<Button>(R.id.nfc_btn).setOnClickListener { _ ->
            val intent = Intent (activity, NfcReaderActivity::class.java)
            activity?.startActivity(intent)
        }

        return view
    }
}
