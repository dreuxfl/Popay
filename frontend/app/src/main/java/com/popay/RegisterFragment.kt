package com.popay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class RegisterFragment : Fragment() {
    private var firstName = ""
    private var lastName = ""
    private var email = ""
    private var password = ""
    private var confirmPassword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        firstName = view?.findViewById<TextView>(R.id.register_first_name)?.text.toString()
        lastName = view?.findViewById<TextView>(R.id.register_last_name)?.text.toString()
        email = view?.findViewById<TextView>(R.id.register_email)?.text.toString()
        password = view?.findViewById<TextView>(R.id.register_password)?.text.toString()
        confirmPassword = view?.findViewById<TextView>(R.id.register_confirm_password)?.text.toString()

        // get reference to button
        val btnRegister = view?.findViewById<Button>(R.id.register)

        /*btn_register.setOnClickListener {
        }*/
        return view
    }


}