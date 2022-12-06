package com.popay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.popay.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.registerEmail.doOnTextChanged { text, _, _, count ->
            val regex = Regex("/\\w+@\\w+.\\w/g")
            if(count > 0){
                if(regex.matches(text.toString())) {
                    binding.registerEmailLayout.error = null
                } else binding.registerEmailLayout.error = "Invalid e-mail"
            }
        }

        binding.registerPassword.doOnTextChanged { text, _, _, _ ->
            if(text!!.length >= 8){
                binding.registerPasswordLayout.error = null
            } else binding.registerPasswordLayout.error = "Password must 8 or more characters long"
        }

        binding.registerPassword1.doOnTextChanged { text, _, _, _ ->
            if(text!!.length >= 8){
                binding.registerPassword1Layout.error = null
            } else binding.registerPasswordLayout.error = "Password must 8 or more characters long"
        }
        return binding.root
    }


}