package com.popay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class LoginFragment : Fragment() {
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        email = view?.findViewById<TextView>(R.id.login_email)?.text.toString()
        password = view?.findViewById<TextView>(R.id.login_password)?.text.toString()

        // get reference to button
        val btnLogin = view?.findViewById<Button>(R.id.login)

        /*btn_login.setOnClickListener {
        }*/
        return view
    }


}