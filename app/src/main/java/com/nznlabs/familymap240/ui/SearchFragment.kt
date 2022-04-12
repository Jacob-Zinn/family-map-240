package com.nznlabs.familymap240.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.nznlabs.familymap240.R
import com.nznlabs.familymap240.adapter.SearchListAdapter
import com.nznlabs.familymap240.databinding.FragmentMapBinding
import com.nznlabs.familymap240.databinding.FragmentSearchBinding
import com.nznlabs.familymap240.viewmodel.MainViewModel
import models.Event
import models.Person
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class SearchFragment : BaseFragment<FragmentSearchBinding>(), SearchListAdapter.Interaction {

    private val mainViewModel by sharedViewModel<MainViewModel>()

    private lateinit var menu: Menu
    private lateinit var searchAdapter: SearchListAdapter


    override fun bind(): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecView()
    }

    private fun initRecView() {

        searchAdapter = SearchListAdapter(
            persons = listOf(),
            events = listOf(),
            this
        )
        binding.recView.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun personSelected(position: Int, person: Person) {
        TODO("Not yet implemented")
    }

    override fun eventSelected(position: Int, event: Event) {
        TODO("Not yet implemented")
    }


}