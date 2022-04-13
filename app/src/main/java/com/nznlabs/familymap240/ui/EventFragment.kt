package com.nznlabs.familymap240.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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


class EventFragment: BaseFragment<FragmentMapBinding>(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View.OnClickListener{

    private val args: EventFragmentArgs by navArgs()
    private lateinit var mMap: GoogleMap
    lateinit var selectedEvent: Event
    private val viewModel by sharedViewModel<MainViewModel>()


    override fun bind(): FragmentMapBinding {
        return FragmentMapBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.eventInfo.root.setOnClickListener(this)
        binding.map.getFragment<SupportMapFragment>().getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener(this)

        populateMap()

        selectedEvent = viewModel.events.value?.get(args.eventID)!!
        val person: Person? = viewModel.persons.value?.get(selectedEvent.personID)
        setEventInfo(selectedEvent, person)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(selectedEvent.latitude.toDouble(), selectedEvent.longitude.toDouble())))
    }

    private fun populateMap() {
        // EVENT TYPES: birth, death, marriage
        viewModel.events.value?.let { events ->
            for (eventItem in events) {
                val color = when(eventItem.value.eventType) {
                    "birth" -> MapFragment.EventColors.BIRTH_COLOR.color
                    "death" -> MapFragment.EventColors.DEATH_COLOR.color
                    "marriage" -> MapFragment.EventColors.MARRIAGE_COLOR.color
                    else -> MapFragment.EventColors.ERROR_COLOR.color
                }

                addMarker(eventItem.value, color)
            }
        }
    }

    private fun addMarker(event: Event, color: Float) {
        val latLng = LatLng(event.latitude.toDouble(), event.longitude.toDouble())

        val marker: Marker? = mMap.addMarker(MarkerOptions().position(latLng).title(event.city).icon(BitmapDescriptorFactory.defaultMarker(color)))
        marker?.tag = event
    }

    private fun drawLine(startEvent: Event, endEvent: Event, color: Int, width: Float) {
        val startPnt = LatLng(startEvent.latitude.toDouble(), startEvent.longitude.toDouble())
        val endPnt = LatLng(endEvent.latitude.toDouble(), endEvent.longitude.toDouble())

        val options = PolylineOptions().add(startPnt).add(endPnt).color(color).width(width)
        val line: Polyline = mMap.addPolyline(options)
        viewModel.addPolyline(line)
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
                drawLine(selectedEvent, spouseEvent, MapFragment.LineColors.SPOUSE.color, 12f)
            }
        }

        // Family tree
        if (settings.showTreeLines) {
            val fatherEvents = events.filter { it.personID == associatedPerson?.fatherID }
            if (!fatherEvents.isNullOrEmpty()) {
                val fatherEvent = fatherEvents.find { it.eventType == "birth" } ?: fatherEvents.minByOrNull { it.year }!!
                drawLine(selectedEvent, fatherEvent, MapFragment.LineColors.TREE.color, 14f)
                drawAncestorLines(associatedPerson!!.fatherID, fatherEvent, events, persons, 2)
            }

            val motherEvents = events.filter { it.personID == associatedPerson?.motherID }
            if (!motherEvents.isNullOrEmpty()) {
                val motherEvent = motherEvents.find { it.eventType == "birth" } ?: motherEvents.minByOrNull { it.year }!!
                drawLine(selectedEvent, motherEvent, MapFragment.LineColors.TREE.color, 14f)
                drawAncestorLines(associatedPerson!!.motherID, motherEvent, events, persons, 2)
            }
        }

    }

    private fun drawAncestorLines(currentPersonID: String, currentEvent: Event, events: List<Event>, persons: List<Person>, generationCount: Int) {
        val currentPerson = persons.find{it.personID == currentPersonID}

        val fatherEvents = events.filter { it.personID == currentPerson?.fatherID }
        if (!fatherEvents.isNullOrEmpty()) {
            val fatherEvent = fatherEvents.find { it.eventType == "birth" } ?: fatherEvents.minByOrNull { it.year }!!
            drawLine(currentEvent, fatherEvent, MapFragment.LineColors.TREE.color, 14f/ generationCount)
            drawAncestorLines(currentPerson!!.fatherID, fatherEvent, events, persons, generationCount + 1)
        }

        val motherEvents = events.filter { it.personID == currentPerson?.motherID }
        if (!motherEvents.isNullOrEmpty()) {
            val motherEvent = motherEvents.find { it.eventType == "birth" } ?: motherEvents.minByOrNull { it.year }!!
            drawLine(currentEvent, motherEvent, MapFragment.LineColors.TREE.color, 14f/ generationCount)
            drawAncestorLines(currentPerson!!.motherID, motherEvent, events, persons, generationCount + 1)
        }
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

    override fun onMarkerClick(marker: Marker): Boolean {

        val event: Event = marker.tag as Event
        selectedEvent = event
        val person: Person? = viewModel.persons.value?.get(event.personID)

        val latLng = LatLng(event.latitude.toDouble(), event.longitude.toDouble())
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))

        setEventInfo(event, person)

        return true
    }

    override fun onClick(v: View?) {
        when (v!!) {
            binding.eventInfo.root -> {
                try {
                    findNavController().navigate(EventFragmentDirections.actionEventFragmentToPersonFragment(personID = selectedEvent.personID))
                } catch (e: NullPointerException) {
                    Timber.e(e, "ERROR: Failed to navigate to person fragment")
                }
            }
        }
    }
}