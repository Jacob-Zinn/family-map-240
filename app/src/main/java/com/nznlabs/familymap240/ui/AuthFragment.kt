package com.nznlabs.familymap240.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.nznlabs.familymap240.R
import com.nznlabs.familymap240.databinding.FragmentAuthBinding
import com.nznlabs.familymap240.repository.ServerProxy
import com.nznlabs.familymap240.session.SessionManager
import com.nznlabs.familymap240.viewmodel.MainViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import requests.LoginRequest
import requests.RegisterRequest
import timber.log.Timber


class AuthFragment : BaseFragment<FragmentAuthBinding>(),
    View.OnClickListener,
    TextWatcher {

    private val viewModel by sharedViewModel<MainViewModel>()
    private val sessionManager: SessionManager by inject()
    private val serverProxy: ServerProxy by inject()

    override fun bind(): FragmentAuthBinding {
        return FragmentAuthBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFakeInput()
        initListeners()
        subscribeObservers()
    }

    @SuppressLint("SetTextI18n")
    private fun initFakeInput() {
        binding.serverHostInputTxt.setText("10.0.2.2") // 66.219.253.90 // 10.0.2.2 //         binding.serverHostInputTxt.setText("localhost/127.0.0.1")
        binding.serverPortInputTxt.setText("8080")
        binding.usernameInputTxt.setText("JacobZinn")
        binding.passwordInputTxt.setText("FamilyMapPass")
        binding.emailInputTxt.setText("Jacobpzinn@gmail.com")
        binding.firstNameInputTxt.setText("Jacob")
        binding.lastNameInputTxt.setText("Zinn")
    }

    private fun initListeners() {
        binding.loginBtn.setOnClickListener(this)
        binding.registerBtn.setOnClickListener(this)

        binding.serverHostInputTxt.addTextChangedListener(this)
        binding.serverPortInputTxt.addTextChangedListener(this)
        binding.usernameInputTxt.addTextChangedListener(this)
        binding.passwordInputTxt.addTextChangedListener(this)
        binding.firstNameInputTxt.addTextChangedListener(this)
        binding.lastNameInputTxt.addTextChangedListener(this)
        binding.emailInputTxt.addTextChangedListener(this)
    }

    private fun subscribeObservers() {

        sessionManager.authToken.observe(viewLifecycleOwner) {
            try {
                findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToMapFragment())
            } catch (e: NullPointerException) {
                Timber.e(e, "ERROR: Failed to navigate to map fragment")
            }
        }
        viewModel.message.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

    }

    private fun checkFieldsForEmptyValues() {
        val tmpLoginRequest = createLoginRequest()
        binding.loginBtn.isEnabled = (tmpLoginRequest != null)

        val tmpRegisterRequest = createRegisterRequest()
        binding.registerBtn.isEnabled = (tmpRegisterRequest != null)
    }

    private fun createLoginRequest(): LoginRequest? {
        val s1: String = binding.serverHostInputTxt.text.toString()
        val s2: String = binding.serverPortInputTxt.text.toString()
        val s3: String = binding.usernameInputTxt.text.toString()
        val s4: String = binding.passwordInputTxt.text.toString()

        return if (!(s1.isEmpty() || s2.isEmpty() || s3.isEmpty() || s4.isEmpty())) {
            LoginRequest(s3, s4)
        } else {
            null
        }
    }

    private fun createRegisterRequest(): RegisterRequest? {
        val tmpLoginRequest = createLoginRequest()

        val s5: String = binding.emailInputTxt.text.toString()
        val s6: String = binding.firstNameInputTxt.text.toString()
        val s7: String = binding.lastNameInputTxt.text.toString()
        val genderID: Int = binding.genderRadioGroup.checkedRadioButtonId

        return if (!(tmpLoginRequest == null || s5.isEmpty() || s6.isEmpty() || s7.isEmpty())) {
            val gender = if (genderID == 1) {
                "m"
            } else {
                "f"
            }
            RegisterRequest(tmpLoginRequest.username, tmpLoginRequest.password, s5, s6, s7, gender)
        } else {
            null
        }
    }

    private fun initHTTPConnectionParams() {
        serverProxy.serverHost = binding.serverHostInputTxt.text.toString()
        serverProxy.serverPort = binding.serverPortInputTxt.text.toString()
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun afterTextChanged(p0: Editable?) {
        checkFieldsForEmptyValues()
    }


    override fun onClick(v: View?) {
        when (v!!) {
            binding.loginBtn -> {
                val loginRequest = createLoginRequest()
                loginRequest?.let {
                    initHTTPConnectionParams()
                    viewModel.login(it)
                }
            }
            binding.registerBtn -> {
                val registerRequest = createRegisterRequest()
                registerRequest?.let {
                    initHTTPConnectionParams()
                    viewModel.register(it)
                }
            }
        }
    }
}
