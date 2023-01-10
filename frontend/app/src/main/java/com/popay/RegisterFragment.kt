package com.popay

import android.content.Context
import android.content.Intent
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
import com.popay.databinding.FragmentRegisterBinding
import org.json.JSONObject

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var baseUrl : String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        baseUrl = context?.getString(R.string.baseUrl)
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.registerEmail.doOnTextChanged { _, _, _, _ ->
            val regex = "[a-zA-Z0-9._-]+@[a-zA-Z0-9-]+\\.[a-zA-Z.]{2,18}".toRegex()
            binding.registerEmailLayout.error = if(binding.registerEmail.text!!.isEmpty()){
                getString(R.string.error_email_required)
            }else if(!regex.matches(binding.registerEmail.text.toString())){
                getString(R.string.error_email_invalid)
            } else null
            binding.register.isEnabled = true
        }
        binding.registerPassword.doOnTextChanged { _, _, _, _ ->
            binding.registerPasswordLayout.error = if(binding.registerPassword.text!!.isEmpty()){
                getString(R.string.error_password_required)
            }else if(binding.registerPassword.text!!.length < 8 ){
                getString(R.string.error_password_invalid)
            } else null
            binding.register.isEnabled = true
        }
        binding.registerPassword1.doOnTextChanged { _, _, _, _ ->
            binding.registerPassword1Layout.error = if(binding.registerPassword1.text!!.isEmpty()){
                getString(R.string.error_password1_required)
            } else if(!binding.registerPassword1.text.contentEquals(binding.registerPassword.text) ){
                getString(R.string.error_passwords_mismatch)
            } else null
            binding.register.isEnabled = true
        }
        binding.registerFullName.doOnTextChanged { _, _, _, _ ->
            binding.registerFullNameLayout.error = if (binding.registerFullName.text!!.isEmpty()){
                getString(R.string.error_full_name_required)
            } else if(binding.registerFullName.text!!.split(" ").size != 2 ){
                getString(R.string.error_full_name_invalid)
            } else null
            binding.register.isEnabled = true
        }

        binding.register.setOnClickListener {
            if(
                binding.registerEmailLayout.error.isNullOrEmpty() &&
                binding.registerPasswordLayout.error.isNullOrEmpty() &&
                binding.registerPassword1Layout.error.isNullOrEmpty() &&
                binding.registerFullNameLayout.error.isNullOrEmpty()

            ) { //if no error send request
                val queue = Volley.newRequestQueue(context)
                val params = HashMap<String, String>()
                params["email"] = binding.registerEmail.text.toString()
                params["password"] = binding.registerPassword.text.toString()
                params["firstName"] = binding.registerFullName.text.toString().split(" ")[0]
                params["lastName"] = binding.registerFullName.text.toString().split(" ")[1]
                val jsonObject = JSONObject(params as Map<*, *>)
                val stringRequest = JsonObjectRequest(
                    Request.Method.POST,
                    "$baseUrl/register",
                    jsonObject,
                    { response ->
                        if(response.getBoolean("success")){
                            Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                            val params1 = HashMap<String, String>()
                            params1["email"] = binding.registerEmail.text.toString()
                            params1["password"] = binding.registerPassword.text.toString()
                            val jsonObject1 = JSONObject(params1 as Map<*, *>)
                            val stringRequest = JsonObjectRequest(
                                Request.Method.POST,
                                "$baseUrl/login",
                                jsonObject1,
                                { resp ->
                                    if(resp.has("token")){
                                        Toast.makeText(context, "Welcome", Toast.LENGTH_LONG).show()
                                        activity?.startActivity(Intent (activity, MainActivity::class.java))
                                        val sharedPref = activity?.getSharedPreferences("Authentication",
                                            Context.MODE_PRIVATE) ?: return@JsonObjectRequest
                                        with (sharedPref.edit()) {
                                            putString("token", resp.getString("token"))
                                            apply()
                                        }
                                        this.activity?.finish()
                                    }else{
                                        Toast.makeText(context, "Invalid email or password", Toast.LENGTH_LONG).show()
                                    }
                                },
                                {
                                    println("LOGIN ERRR ${it.message} ${it.cause} ${it.networkResponse.toString()} ")
                                }
                            )
                            queue.add(stringRequest)
                        } else {
                            Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    },
                    {
                        println("REGISTER ERRR $it ${it.networkResponse} ${jsonObject.toString()}")

                    }
                )
                queue.add(stringRequest)

            } else {
                Toast.makeText(context, "Register failed", Toast.LENGTH_LONG).show()
            }
        }
        return binding.root
    }


}