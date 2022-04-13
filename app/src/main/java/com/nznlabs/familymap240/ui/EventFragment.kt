package com.nznlabs.familymap240.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
import com.nznlabs.familymap240.viewmodel.MainViewModel
import models.Event
import models.Person
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.lang.Exception


class EventFragment: BaseFragment<FragmentMapBinding>(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View.OnClickListener{


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
                    "birth" -> EventColors.BIRTH_COLOR.color
                    "death" -> EventColors.DEATH_COLOR.color
                    "marriage" -> EventColors.MARRIAGE_COLOR.color
                    else -> EventColors.ERROR_COLOR.color
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