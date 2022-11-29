package com.popay.ui.main.ui.register

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.popay.R
import com.popay.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private lateinit var registerViewModel: RegisterViewModel
    private var _binding: FragmentRegisterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RegisterViewModel = ViewModelProvider(this, RegisterViewModelFactory())
            .get(RegisterViewModel::class.java)

        val usernameEditText = binding.username
        val passwordEditText = binding.password
        val RegisterButton = binding.Register
        val loadingProgressBar = binding.loading

        RegisterViewModel.RegisterFormState.observe(viewLifecycleOwner,
            Observer { RegisterFormState ->
                if (RegisterFormState == null) {
                    return@Observer
                }
                RegisterButton.isEnabled = RegisterFormState.isDataValid
                RegisterFormState.usernameError?.let {
                    usernameEditText.error = getString(it)
                }
                RegisterFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
                }
            })

        RegisterViewModel.RegisterResult.observe(viewLifecycleOwner,
            Observer { RegisterResult ->
                RegisterResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                RegisterResult.error?.let {
                    showRegisterFailed(it)
                }
                RegisterResult.success?.let {
                    updateUiWithUser(it)
                }
            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                RegisterViewModel.RegisterDataChanged(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
        }
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                RegisterViewModel.Register(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
            false
        }

        RegisterButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            RegisterViewModel.Register(
                usernameEditText.text.toString(),
                passwordEditText.text.toString()
            )
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome) + model.displayName
        // TODO : initiate successful logged in experience
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
    }

    private fun showRegisterFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}