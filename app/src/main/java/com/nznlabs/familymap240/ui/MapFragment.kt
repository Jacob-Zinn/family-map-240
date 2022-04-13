package com.nznlabs.familymap240.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.nznlabs.familymap240.R
import com.nznlabs.familymap240.databinding.FragmentMapBinding
import com.nznlabs.familymap240.viewmodel.MainViewModel
import models.Event
import models.Person
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber


class MapFragment: BaseFragment<FragmentMapBinding>(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    enum class EventColors(val color: Float) {
        BIRTH_COLOR(BitmapDescriptorFactory.HUE_GREEN),
        DEATH_COLOR(BitmapDescriptorFactory.HUE_VIOLET),
        MARRIAGE_COLOR(BitmapDescriptorFactory.HUE_ROSE),
        ERROR_COLOR(BitmapDescriptorFactory.HUE_ORANGE),
    }

    enum class LineColors(val color: Int) {
        SPOUSE(R.color.death_color),
        TREE(R.color.blue_navy),
        LIFE_STORY(R.color.orange),
        ERROR_COLOR(R.color.black)
    }

    private lateinit var mMap: GoogleMap
    private lateinit var menu: Menu
    private val viewModel by sharedViewModel<MainViewModel>()

    override fun bind(): FragmentMapBinding {
        return FragmentMapBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding.map.getFragment<SupportMapFragment>().getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener(this)

        populateMap()
        // Add a marker in United States and move the camera
        val us = LatLng(39.0, -100.0)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(us))
    }

    private fun populateMap() {
        // EVENT TYPES: birth, death, marriage
        viewModel.events.value?.let { events ->
            for (eventItem in events) {
                val color = when(eventItem.value.eventType) {
                    "birth" -> EventColors.BIRTH_COLOR.color
                    "death" -> EventColors.DEATH_COLOR.color
                    "marriage" -> EventColors.MARRIAGE_COLOR.color
                    else -> EventColors.ERROR_COLOR.color
                }

                addMarker(eventItem.value, color)
            }
        }
    }

    private fun clearMap() {
        mMap.clear()
    }

    private fun addMarker(event: Event, color: Float) {
        val latLng = LatLng(event.latitude.toDouble(), event.longitude.toDouble())

        val marker: Marker? = mMap.addMarker(MarkerOptions().position(latLng).title(event.city).icon(BitmapDescriptorFactory.defaultMarker(color)))
        marker?.tag = event
    }

    private fun drawLine(startEvent: Event, endEvent: Event, color: Float, width: Float) {
        val startPnt = LatLng(startEvent.latitude.toDouble(), startEvent.longitude.toDouble())
        val endPnt = LatLng(endEvent.latitude.toDouble(), endEvent.longitude.toDouble())

        val options = PolylineOptions().add(startPnt).add(endPnt).color(R.color.blue_navy).width(width)
        val line: Polyline = mMap.addPolyline(options)
        viewModel.addPolyline(line)

    }

    @SuppressLint("SetTextI18n")
    private fun setEventInfo(event: Event, person: Person?) {
        person?.let {
            binding.eventInfo.name.text = "${person.firstName} ${person.lastName}"
            if (person.gender == "m") {
                binding.eventInfo.genderImg.setImageResource(R.drawable.ic_baseline_male_blue_24)
            } else {
                binding.eventInfo.genderImg.setImageResource(R.drawable.ic_round_female_red_24)
            }
        }
        binding.eventInfo.eventInfo.text = "${event.eventType.uppercase()}: ${event.city}, ${event.country} (${event.year})"
        binding.eventInfo.root.isVisible = true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        inflater.inflate(R.menu.map_menu, this.menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // option selected from action bar menu
        when (item.itemId) {
            R.id.action_settings -> {
                try {
                    findNavController().navigate(MapFragmentDirections.actionMapFragmentToSettingsFragment())
                } catch (e: NullPointerException) {
                    Timber.e(e, "ERROR: Failed to navigate to settings fragment")
                }
            }
            R.id.action_search -> {
                try {
                    findNavController().navigate(MapFragmentDirections.actionMapFragmentToSearchFragment())
                } catch (e: NullPointerException) {
                    Timber.e(e, "ERROR: Failed to navigate to search fragment")
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n")
    override fun onMarkerClick(marker: Marker): Boolean {

        val event: Event = marker.tag as Event
        val person: Person? = viewModel.persons.value?.get(event.personID)

        setEventInfo(event, person)

        return true
    }
}