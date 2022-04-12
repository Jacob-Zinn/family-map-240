package com.nznlabs.familymap240.ui

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import com.nznlabs.familymap240.databinding.FragmentSettingsBinding
import com.nznlabs.familymap240.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber


class SettingsFragment : BaseFragment<FragmentSettingsBinding>(),
     CompoundButton.OnCheckedChangeListener {

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun bind(): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        subscribeObservers()
    }


    private fun initListeners() {
        binding.lifeLines.toggle.setOnCheckedChangeListener(this)
    }

    private fun subscribeObservers() {

    }

    override fun onCheckedChanged(v: CompoundButton?, isChecked: Boolean) {
        when (v!!) {
            binding.lifeLines.toggle -> {
                Timber.d("zzz CHECKING : ${isChecked}")
            }
        }
    }
}
