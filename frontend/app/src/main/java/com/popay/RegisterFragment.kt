package com.popay

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

    private val baseUrl = "http://10.136.76.77:8080/api"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


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
                params["first_name"] = binding.registerFullName.text.toString().split(" ")[0]
                params["last_name"] = binding.registerFullName.text.toString().split(" ")[1]
                val jsonObject = JSONObject(params as Map<*, *>)
                val stringRequest = JsonObjectRequest(
                    Request.Method.POST,
                    "$baseUrl/register",
                    jsonObject,
                    { response ->
                        println(response.toString())
                        Toast.makeText(context, response.toString(), Toast.LENGTH_LONG).show()
                    },
                    {
                        println("ERRR ${it.toString()}")

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