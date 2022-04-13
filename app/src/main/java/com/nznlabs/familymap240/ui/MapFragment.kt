package com.nznlabs.familymap240.ui

import android.annotation.SuppressLint
import android.graphics.Color
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
import com.nznlabs.familymap240.model.Settings
import com.nznlabs.familymap240.viewmodel.MainViewModel
import models.Event
import models.Person
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber


class MapFragment : BaseFragment<FragmentMapBinding>(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    View.OnClickListener {

    enum class EventColors(val color: Float) {
        BIRTH_COLOR(BitmapDescriptorFactory.HUE_GREEN),
        DEATH_COLOR(BitmapDescriptorFactory.HUE_VIOLET),
        MARRIAGE_COLOR(BitmapDescriptorFactory.HUE_ROSE),
        ERROR_COLOR(BitmapDescriptorFactory.HUE_ORANGE),
    }

    enum class LineColors(val color: Int) {
        SPOUSE(Color.MAGENTA),
        TREE(Color.BLUE),
        LIFE_STORY(Color.YELLOW),
        ERROR_COLOR(Color.BLACK)
    }

    private lateinit var mMap: GoogleMap
    private lateinit var menu: Menu
    private val viewModel by sharedViewModel<MainViewModel>()

    override fun bind(): FragmentMapBinding {
        return FragmentMapBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeObservers()
        binding.eventInfo.root.setOnClickListener(this)
        setHasOptionsMenu(true)
        binding.map.getFragment<SupportMapFragment>().getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener(this)

        clearMap()
        populateMap()

        // Add a marker in United States and move the camera
        val us = LatLng(39.0, -100.0)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(us))
    }

    private fun subscribeObservers() {
        viewModel.selectedEvent.observe(viewLifecycleOwner) { event ->
            val person: Person? = viewModel.persons.value?.get(event.personID)
            setEventInfo(event, person)
            addLines(
                selectedEvent = viewModel.selectedEvent.value!!,
                viewModel.events.value!!.values.toList(),
                viewModel.persons.value!!.values.toList(),
                viewModel.settings.value!!
            )
        }
    }

    private fun populateMap() {
        // EVENT TYPES: birth, death, marriage
        viewModel.events.value?.let { events ->
            for (eventItem in events.values) {
                val color = when (eventItem.eventType) {
                    "birth" -> EventColors.BIRTH_COLOR.color
                    "death" -> EventColors.DEATH_COLOR.color
                    "marriage" -> EventColors.MARRIAGE_COLOR.color
                    else -> EventColors.ERROR_COLOR.color
                }
                addMarker(eventItem, color)
            }

            viewModel.selectedEvent.value?.let {
                addLines(
                    selectedEvent = it,
                    events.values.toList(),
                    viewModel.persons.value!!.values.toList(),
                    viewModel.settings.value!!
                )
            }
        }

    }

    private fun clearMap() {
        mMap.clear()
    }

    private fun addMarker(event: Event, color: Float) {
        val latLng = LatLng(event.latitude.toDouble(), event.longitude.toDouble())

        val marker: Marker? = mMap.addMarker(
            MarkerOptions().position(latLng).title(event.city).icon(BitmapDescriptorFactory.defaultMarker(color))
        )
        marker?.tag = event
    }

    private fun addLines(selectedEvent: Event, events: List<Event>, persons: List<Person>, settings: Settings) {
        // clearing
        for (line in viewModel.lines) {
            line.remove()
        }

        val associatedPerson = persons.find { it.personID == selectedEvent.personID }

        // spouse
        if (settings.showSpouseLines) {
            val spouseEvents = events.filter { it.personID == associatedPerson?.spouseID }
            if (!spouseEvents.isNullOrEmpty()) {
                val spouseEvent =
                    spouseEvents.find { it.eventType == "birth" } ?: spouseEvents.minByOrNull { it.year }!!
                drawLine(selectedEvent, spouseEvent, LineColors.SPOUSE.color, 12f)
            }
        }

        // Family tree
        if (settings.showTreeLines) {
            val fatherEvents = events.filter { it.personID == associatedPerson?.fatherID }
            if (!fatherEvents.isNullOrEmpty()) {
                val fatherEvent = fatherEvents.find { it.eventType == "birth" } ?: fatherEvents.minByOrNull { it.year }!!
                drawLine(selectedEvent, fatherEvent, LineColors.TREE.color, 14f)
                drawAncestorLines(associatedPerson!!.fatherID, fatherEvent, events, persons, 2)
            }

            val motherEvents = events.filter { it.personID == associatedPerson?.motherID }
            if (!motherEvents.isNullOrEmpty()) {
                val motherEvent = motherEvents.find { it.eventType == "birth" } ?: motherEvents.minByOrNull { it.year }!!
                drawLine(selectedEvent, motherEvent, LineColors.TREE.color, 14f)
                drawAncestorLines(associatedPerson!!.motherID, motherEvent, events, persons, 2)
            }
        }

    }

    private fun drawAncestorLines(currentPersonID: String, currentEvent: Event, events: List<Event>, persons: List<Person>, generationCount: Int) {
        val currentPerson = persons.find{it.personID == currentPersonID}

        val fatherEvents = events.filter { it.personID == currentPerson?.fatherID }
        if (!fatherEvents.isNullOrEmpty()) {
            val fatherEvent = fatherEvents.find { it.eventType == "birth" } ?: fatherEvents.minByOrNull { it.year }!!
            drawLine(currentEvent, fatherEvent, LineColors.TREE.color, 14f/ generationCount)
            drawAncestorLines(currentPerson!!.fatherID, fatherEvent, events, persons, generationCount + 1)
        }

        val motherEvents = events.filter { it.personID == currentPerson?.motherID }
        if (!motherEvents.isNullOrEmpty()) {
            val motherEvent = motherEvents.find { it.eventType == "birth" } ?: motherEvents.minByOrNull { it.year }!!
            drawLine(currentEvent, motherEvent, LineColors.TREE.color, 14f/ generationCount)
            drawAncestorLines(currentPerson!!.motherID, motherEvent, events, persons, generationCount + 1)
        }
    }


    private fun drawLine(startEvent: Event, endEvent: Event, color: Int, width: Float) {
        val startPnt = LatLng(startEvent.latitude.toDouble(), startEvent.longitude.toDouble())
        val endPnt = LatLng(endEvent.latitude.toDouble(), endEvent.longitude.toDouble())

        val options = PolylineOptions().add(startPnt).add(endPnt).color(color).width(width)
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
        binding.eventInfo.eventInfo.text =
            "${event.eventType.uppercase()}: ${event.city}, ${event.country} (${event.year})"
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
        viewModel.setSelectedEvent(marker.tag as Event)
        return true
    }

    override fun onClick(v: View?) {
        when (v!!) {
            binding.eventInfo.root -> {
                try {
                    findNavController().navigate(MapFragmentDirections.actionMapFragmentToPersonFragment(personID = viewModel.selectedEvent.value!!.personID))
                } catch (e: NullPointerException) {
                    Timber.e(e, "ERROR: Failed to navigate to person fragment")
                }
            }
        }
    }
}