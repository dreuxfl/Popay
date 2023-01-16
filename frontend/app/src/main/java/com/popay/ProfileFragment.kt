package com.popay

import android.content.Context
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
import com.popay.databinding.FragmentProfileBinding
import org.json.JSONObject

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var baseUrl : String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        baseUrl = context?.getString(R.string.baseUrl)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.profileEmail.doOnTextChanged { _, _, _, _ ->
            val regex = "[a-zA-Z0-9._-]+@[a-zA-Z0-9-]+\\.[a-zA-Z.]{2,18}".toRegex()
            binding.profileEmailLayout.error = if(binding.profileEmail.text!!.isEmpty()){
                getString(R.string.error_email_required)
            }else if(!regex.matches(binding.profileEmail.text.toString())){
                getString(R.string.error_email_invalid)
            } else null
            binding.editProfile.isEnabled = true
        }
        binding.profilePassword.doOnTextChanged { _, _, _, _ ->
            binding.profilePasswordLayout.error = if(binding.profilePassword.text!!.isEmpty()){
                getString(R.string.error_password_required)
            }else if(binding.profilePassword.text!!.length < 8 ){
                getString(R.string.error_password_invalid)
            } else null
            binding.editProfile.isEnabled = true
        }
        binding.profilePassword1.doOnTextChanged { _, _, _, _ ->
            binding.profilePassword1Layout.error = if(binding.profilePassword1.text!!.isEmpty()){
                getString(R.string.error_password1_required)
            } else if(!binding.profilePassword1.text.contentEquals(binding.profilePassword.text) ){
                getString(R.string.error_passwords_mismatch)
            } else null
            binding.editProfile.isEnabled = true
        }
        binding.profileFullName.doOnTextChanged { _, _, _, _ ->
            binding.profileFullNameLayout.error = if (binding.profileFullName.text!!.isEmpty()){
                getString(R.string.error_full_name_required)
            } else if(binding.profileFullName.text!!.split(" ").size != 2 ){
                getString(R.string.error_full_name_invalid)
            } else null
            binding.editProfile.isEnabled = true
        }

        binding.editProfile.setOnClickListener {
            if(
                binding.profileEmailLayout.error.isNullOrEmpty() &&
                binding.profilePasswordLayout.error.isNullOrEmpty() &&
                binding.profilePassword1Layout.error.isNullOrEmpty() &&
                binding.profileFullNameLayout.error.isNullOrEmpty()

            ) {
                val queue = Volley.newRequestQueue(context)
                val params = HashMap<String, String>()
                params["email"] = binding.profileEmail.text.toString()
                params["password"] = binding.profilePassword.text.toString()
                params["firstName"] = binding.profileFullName.text.toString().split(" ")[0]
                params["lastName"] = binding.profileFullName.text.toString().split(" ")[1]
                val jsonObject = JSONObject(params as Map<*, *>)
                val stringRequest = JsonObjectRequest(
                    Request.Method.PUT,
                    "$baseUrl/user",
                    jsonObject,
                    { response ->
                        if(response.getBoolean("success")){
                            Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    },
                    {
                        println("profile ERRR $it ${it.networkResponse} ${jsonObject.toString()}")

                    }
                )
                queue.add(stringRequest)

            } else {
                Toast.makeText(context, "profile failed", Toast.LENGTH_LONG).show()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserData()
    }

    private fun getUserData() {
        val sharedPreferences: SharedPreferences? = context?.getSharedPreferences("Authentication", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("token", null)
        val queue = Volley.newRequestQueue(context)

        val arrayRequest : JsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            "$baseUrl/profile",
            null,
            { response ->
                try{

                    binding.profileEmail.setText(response.getString("email").toString())
                    binding.profileFullName.setText("${response.getJSONObject("firstName")} ${response.getString("lastName")}")
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Fatal error, call dev", Toast.LENGTH_LONG).show()
                }
            },
            {
                println("ERRR ${it}")
            },

            ) {
            override fun getHeaders(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Bearer $token"
                return params
            }
        }
        queue.add(arrayRequest)

    }

}