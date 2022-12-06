package com.popay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.popay.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.loginEmail.doOnTextChanged { text, start, before, count ->
            var regex = Regex("/\\w+@\\w+.\\w/g")
            if(count > 0){
                if(regex.matches(text.toString())) {
                    binding.loginEmailLayout.error = null
                } else binding.loginEmailLayout.error = "Invalid e-mail"
            }
        }
        return binding.root
    }


}