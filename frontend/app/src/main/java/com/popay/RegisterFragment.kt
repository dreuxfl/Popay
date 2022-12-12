package com.popay

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnDetach
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.popay.databinding.FragmentRegisterBinding
import java.io.Console
import java.io.StringReader

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.registerEmail.doOnTextChanged { _, _, _, _ ->
            val regex = Regex("/\\s+@\\s+.\\s/g")
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
            ) { //if no error

            } else {
                val errorToast = Toast.makeText(context, "Register failed", Toast.LENGTH_LONG)
                errorToast.setGravity(Gravity.BOTTOM, 50, 50)
                errorToast.show()
            }
        }
        return binding.root
    }


}