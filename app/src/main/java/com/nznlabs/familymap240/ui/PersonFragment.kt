package com.nznlabs.familymap240.ui

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.nznlabs.familymap240.adapter.PersonListAdapter
import com.nznlabs.familymap240.databinding.FragmentPersonBinding
import com.nznlabs.familymap240.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class PersonFragment : BaseFragment<FragmentPersonBinding>()  {

    private val viewModel by sharedViewModel<MainViewModel>()
    private val args: PersonFragmentArgs by navArgs()
    private lateinit var personAdapter: PersonListAdapter

    override fun bind(): FragmentPersonBinding {
        return FragmentPersonBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListView()
    }


    private fun initListView() {

    }


}