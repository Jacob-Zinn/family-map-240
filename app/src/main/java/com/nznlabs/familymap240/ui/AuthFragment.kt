package com.nznlabs.familymap240.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nznlabs.familymap240.R
import com.nznlabs.familymap240.databinding.FragmentAuthBinding
import com.nznlabs.familymap240.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import models.AuthToken
import requests.LoginRequest
import timber.log.Timber


@AndroidEntryPoint
class AuthFragment : BaseFragment(),
    View.OnClickListener,
    TextWatcher {

    val viewModel: AuthViewModel by viewModels()

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    fun initListeners() {
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

    fun checkFieldsForEmptyValues() {
        val s1: String = binding.serverHostInputTxt.text.toString()
        val s2: String = binding.serverPortInputTxt.text.toString()
        val s3: String = binding.usernameInputTxt.text.toString()
        val s4: String = binding.passwordInputTxt.text.toString()

        binding.loginBtn.isEnabled = !(s1.isEmpty() || s2.isEmpty() || s3.isEmpty() || s4.isEmpty())

        val s5: String = binding.firstNameInputTxt.text.toString()
        val s6: String = binding.lastNameInputTxt.text.toString()
        val s7: String = binding.emailInputTxt.text.toString()
        val genderID: Int = binding.genderRadioGroup.checkedRadioButtonId
        Timber.d("gender id " + genderID)

        binding.registerBtn.isEnabled = !(s1.isEmpty() || s2.isEmpty() || s3.isEmpty() || s4.isEmpty() || s5.isEmpty() || s6.isEmpty() || s7.isEmpty())


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun afterTextChanged(p0: Editable?) {
        checkFieldsForEmptyValues()
    }


    override fun onClick(v: View?) {
        when (v!!) {
            binding.loginBtn -> {
                try {
                    findNavController().navigate(R.id.action_authFragment_to_mapFragment)
                } catch (e: NullPointerException) {
                    Timber.e(
                        e,
                        "navigateToSessionsFragmentFromRecViewSelection: ERROR: Failed to navigate"
                    )
                }
            }
        }
    }
}
