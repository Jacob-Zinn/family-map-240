package com.nznlabs.familymap240.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nznlabs.familymap240.R
import com.nznlabs.familymap240.databinding.FragmentAuthBinding
import com.nznlabs.familymap240.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AuthFragment : BaseFragment(),
View.OnClickListener{

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

        binding.launchBtn.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v!!) {
            binding.launchBtn -> {
                try {
                    findNavController().navigate(R.id.action_authFragment_to_mapFragment)
                } catch (e: NullPointerException) {
                    Timber.e(e, "navigateToSessionsFragmentFromRecViewSelection: ERROR: Failed to navigate")
                }
            }
        }
    }
}