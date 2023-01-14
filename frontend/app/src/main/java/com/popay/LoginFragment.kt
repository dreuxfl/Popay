package com.popay


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.auth0.android.jwt.JWT

import com.popay.databinding.FragmentLoginBinding
import com.popay.entities.User
import org.json.JSONObject

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val baseUrl = "http://10.136.76.77:8080/api"
    private val token : SharedPreferences? = null
    private var baseUrl : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        baseUrl = context?.getString(R.string.baseUrl)

        val regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$".toRegex()
        binding.loginEmail.doOnTextChanged { _, _, _, _ ->
            binding.loginEmailLayout.error = if(binding.loginEmail.text!!.isEmpty()){
                getString(R.string.error_email_required)
            }else if(!regex.matches(binding.loginEmail.text.toString())){
                println(binding.loginEmail.text.toString())
                getString(R.string.error_email_invalid)
            } else null
            binding.login.isEnabled = true
        }
        binding.loginPassword.doOnTextChanged { _, _, _, _ ->
            binding.loginPasswordLayout.error = if(binding.loginPassword.text!!.isEmpty()){
                getString(R.string.error_password_required)
            }else if(binding.loginPassword.text!!.length < 8 ){
                getString(R.string.error_password_invalid)
            } else null
            binding.login.isEnabled = true
        }

        binding.login.setOnClickListener(View.OnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()
            if(
                binding.loginEmailLayout.error.isNullOrEmpty() &&
                binding.loginPasswordLayout.error.isNullOrEmpty()  ){

                val queue = Volley.newRequestQueue(context)
                val params = HashMap<String, String>()
                params["email"] = email
                params["password"] = password

                val jsonObject = JSONObject(params as Map<*, *>)
                val stringRequest = JsonObjectRequest(
                    Request.Method.POST,
                    "$baseUrl/login",
                    jsonObject,
                    { response ->
                        if(response.has("token")){
                            val jwt = JWT(response.getString("token"))
                            val fullName = jwt.getClaim("firstName").asString() + " " + jwt.getClaim("lastName").asString()
                            val userId = jwt.getClaim("userId").asInt()

                            val user = userId?.let { it1 -> User(it1, fullName, email) }

                            Toast.makeText(context, "Welcome", Toast.LENGTH_LONG).show()

                            val intent = Intent(activity, MainActivity::class.java)
                            intent.putExtra("user", user)
                            startActivity(intent)


                            val sharedPref = activity?.getSharedPreferences("Authentication",Context.MODE_PRIVATE) ?: return@JsonObjectRequest
                            with (sharedPref.edit()) {
                                putString("token", response.getString("token"))
                                apply()
                            }
                            this.activity?.finish()
                        }else{
                            Toast.makeText(context, "Invalid email or password", Toast.LENGTH_LONG).show()
                        }
                    },
                    {
                        println("ERRR ${it.toString()}")
                    }
                )
                queue.add(stringRequest)

            } else {
                Toast.makeText(context, "Register failed", Toast.LENGTH_LONG).show()
            }

        })

        return binding.root
    }


}