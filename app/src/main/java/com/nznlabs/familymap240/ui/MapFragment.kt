package com.nznlabs.familymap240.ui

import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.nznlabs.familymap240.databinding.FragmentMapBinding
import com.nznlabs.familymap240.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class MapFragment: BaseFragment<FragmentMapBinding>(), OnMapReadyCallback {


    private lateinit var mMap: GoogleMap
    private val mainViewModel by sharedViewModel<MainViewModel>()


    override fun bind(): FragmentMapBinding {
        return FragmentMapBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init map
        binding.map.getFragment<SupportMapFragment>().getMapAsync(this)
    }

//    private fun clearBackStack() {
//        val fm: FragmentManager = requireActivity().supportFragmentManager
//        for (i in 0 until fm.backStackEntryCount) {
//            fm.popBackStack()
//        }
//    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in United States and move the camera
        val us = LatLng(39.0, -100.0)
        mMap.addMarker(MarkerOptions().position(us).title("Marker in middle of US"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(us))
    }

}