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
import com.popay.databinding.FragmentProfileBinding
import org.json.JSONObject

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var baseUrl : String? = null
    private var edit_mode = false
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

        binding.logout.setOnClickListener{
            logout()
        }



        binding.editProfile.setOnClickListener {
            if(edit_mode){
                editProfile()
                binding.editProfile.text = "Edit Profile"
                binding.profileEmail.isEnabled = false
                binding.profilePasswordLayout.visibility = View.GONE
                binding.profilePassword1Layout.visibility = View.GONE
                binding.profileFullName.isEnabled = false
                edit_mode = true
            }else{
                binding.editProfile.text = "Save"
                binding.profileEmail.isEnabled = true
                binding.profilePasswordLayout.visibility = View.VISIBLE
                binding.profilePassword1Layout.visibility = View.VISIBLE
                binding.profileFullName.isEnabled = true
                edit_mode = true
            }
        }




        return binding.root
    }

    fun editProfile(){
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
            val sharedPreferences: SharedPreferences? = context?.getSharedPreferences("Authentication", Context.MODE_PRIVATE)
            val token = sharedPreferences?.getString("token", null)
            val stringRequest = object : JsonObjectRequest(
                Method.PUT,
                "$baseUrl/user",
                jsonObject,
                { response ->
                    if(response.getBoolean("success")){
                        Toast.makeText(context, "Profile Edition successful", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                },
                {
                    Toast.makeText(context, "Profile edit error", Toast.LENGTH_SHORT).show()
                    println("profile ERRR $it ${it.networkResponse} ${jsonObject.toString()}")

                }
            ){
                override fun getHeaders(): MutableMap<String, String> {
                    val params1: MutableMap<String, String> = HashMap()
                    params1["Authorization"] = "Bearer $token"
                    return params1
                }
            }
            queue.add(stringRequest)


        } else {
            Toast.makeText(context, "All fields must be filled in", Toast.LENGTH_LONG).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserData()
    }

    fun logout(){
        println("logout")
        val sharedPreferences: SharedPreferences? = context?.getSharedPreferences("Authentication", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.remove("token")
        editor?.apply()
        val intent = Intent(context, AuthActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun getUserData() {
        val sharedPreferences: SharedPreferences? = context?.getSharedPreferences("Authentication", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("token", null)
        val queue = Volley.newRequestQueue(context)

        val fetchRequest : JsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            "$baseUrl/user",
            null,
            { response ->
                try{

                    binding.profileEmail.setText(response.getString("email").toString())
                    val fullName = "${response.getString("firstName")} ${response.getString("lastName")}"
                    binding.profileFullName.setText(fullName)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Fatal error, call dev", Toast.LENGTH_LONG).show()
                }
            },
            {
                println("ERRR ${it}")
            }

        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Bearer $token"
                return params
            }
        }
        queue.add(fetchRequest)

    }

}