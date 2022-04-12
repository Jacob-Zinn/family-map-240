package com.nznlabs.familymap240.ui

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.navigation.fragment.findNavController
import com.nznlabs.familymap240.databinding.FragmentSettingsBinding
import com.nznlabs.familymap240.session.SessionManager
import com.nznlabs.familymap240.viewmodel.MainViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber


class SettingsFragment : BaseFragment<FragmentSettingsBinding>(),
    CompoundButton.OnCheckedChangeListener {

    private val viewModel by sharedViewModel<MainViewModel>()
    private val sessionManager: SessionManager by inject()

    override fun bind(): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeObservers()
        initListeners()
    }

    private fun subscribeObservers() {
        sessionManager.authToken.observe(viewLifecycleOwner) {
            if (it == null) {
                try {
                    findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToAuthFragment())
                } catch (e: NullPointerException) {
                    Timber.e(e, "ERROR: Failed to navigate upon logout")
                }
            }
        }

        viewModel.settings.observe(viewLifecycleOwner) {
            binding.lifeStoryLines.toggle.isChecked = it.showLifeStoryLines
            binding.treeLines.toggle.isChecked = it.showTreeLines
            binding.spouseLines.toggle.isChecked = it.showSpouseLines
            binding.fatherSide.toggle.isChecked = it.fatherSide
            binding.motherSide.toggle.isChecked = it.motherSide
            binding.maleEvents.toggle.isChecked = it.maleEvents
            binding.femaleEvents.toggle.isChecked = it.femaleEvents
        }
    }

    private fun initListeners() {
        binding.lifeStoryLines.toggle.setOnCheckedChangeListener(this)
        binding.treeLines.toggle.setOnCheckedChangeListener(this)
        binding.spouseLines.toggle.setOnCheckedChangeListener(this)
        binding.fatherSide.toggle.setOnCheckedChangeListener(this)
        binding.motherSide.toggle.setOnCheckedChangeListener(this)
        binding.maleEvents.toggle.setOnCheckedChangeListener(this)
        binding.femaleEvents.toggle.setOnCheckedChangeListener(this)
        binding.logout.setOnClickListener {
            Timber.d("LOGGING OUT")
            viewModel.logout()
        }
    }

    override fun onCheckedChanged(v: CompoundButton?, isChecked: Boolean) {
        when (v!!) {
            binding.lifeStoryLines.toggle -> {
                viewModel.settings.value?.let {
                    val newSettings = it
                    newSettings.showLifeStoryLines = isChecked
                    viewModel.setSettings(newSettings)
                }
            }
            binding.treeLines.toggle -> {
                  viewModel.settings.value?.let {
                    val newSettings = it
                    newSettings.showTreeLines = isChecked
                    viewModel.setSettings(newSettings)
                }
            }
            binding.spouseLines.toggle -> {
                  viewModel.settings.value?.let {
                    val newSettings = it
                    newSettings.showSpouseLines = isChecked
                    viewModel.setSettings(newSettings)
                }
            }
            binding.fatherSide.toggle -> {
                  viewModel.settings.value?.let {
                    val newSettings = it
                    newSettings.fatherSide = isChecked
                    viewModel.setSettings(newSettings)
                }
            }
            binding.motherSide.toggle -> {
                  viewModel.settings.value?.let {
                    val newSettings = it
                    newSettings.motherSide = isChecked
                    viewModel.setSettings(newSettings)
                }
            }
            binding.maleEvents.toggle -> {
                  viewModel.settings.value?.let {
                    val newSettings = it
                    newSettings.maleEvents = isChecked
                    viewModel.setSettings(newSettings)
                }
            }
            binding.femaleEvents.toggle -> {
                  viewModel.settings.value?.let {
                    val newSettings = it
                    newSettings.femaleEvents = isChecked
                    viewModel.setSettings(newSettings)
                }
            }
        }
    }
}
