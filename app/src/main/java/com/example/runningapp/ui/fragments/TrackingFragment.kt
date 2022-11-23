package com.example.runningapp.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.runningapp.R
import com.example.runningapp.databinding.FragmentTrackingBinding
import com.example.runningapp.other.Constants.ACTION_PAUSE_SERVICE
import com.example.runningapp.services.TrackingService
import com.example.runningapp.ui.viewmodels.MainViewModel
import com.google.android.gms.maps.GoogleMap
import dagger.hilt.android.AndroidEntryPoint
import com.example.runningapp.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningapp.other.Constants.ACTION_STOP_SERVICE
import com.example.runningapp.other.Constants.MAP_ZOOM
import com.example.runningapp.other.Constants.POLYLINE_COLOR
import com.example.runningapp.other.Constants.POLYLINE_WIDTH
import com.example.runningapp.other.TrackingUtility
import com.example.runningapp.services.Polyline
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import timber.log.Timber

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {
    private val viewModel : MainViewModel by viewModels()
    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()

    lateinit var binding: FragmentTrackingBinding
    // This is the actual map object whereas the MapView is where this map is displayed
    private var map : GoogleMap? = null

    private var curTimeInMillis = 0L

    private var menu: Menu? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentTrackingBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        val menuHost : MenuHost = requireActivity()
        menuHost.addMenuProvider(object :MenuProvider {

            override fun onPrepareMenu(menu: Menu) {
                super.onPrepareMenu(menu)
                if(curTimeInMillis >0L){
                    menu?.getItem(0)?.isVisible = true
                }
            }
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_tracking_menu,menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                val dialog = MaterialAlertDialogBuilder(requireContext(),R.style.AlertDialogTheme)
                    .setTitle("Cancel the run?")
                    .setMessage("Are you sure to cancel the current run and delete all its data?")
                    .setIcon(R.drawable.ic_delete)
                    .setPositiveButton("Yes"){ _,_->
                        stopRun()
                    }
                    .setNegativeButton("No"){ dialogInterface,_->
                        dialogInterface.cancel()
                    }
                    .create()
                dialog.show()
                return true
            }
        },viewLifecycleOwner,Lifecycle.State.RESUMED)

        binding.apply {
            btnToggleRun.setOnClickListener {
                toggleRun()
            }
            mapView.onCreate(savedInstanceState)
            mapView.getMapAsync {
                map = it
                addAllPolylines() // That is only called when the fragment is created - so if the phone is rotated this block is called again
            }
        }
        subscribeToObservers()
    }

    private fun stopRun(){
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner,Observer{
            updateTracking(it)
        })
        TrackingService.pathPoints.observe(viewLifecycleOwner,Observer{
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner,Observer{
            curTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeInMillis,true)
            binding.tvTimer.text = formattedTime
        })
    }

    private fun toggleRun() {
        if (isTracking){
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun updateTracking(isTracking : Boolean){
        this.isTracking = isTracking
        if(!isTracking){
            binding.apply {
                btnToggleRun.text = "Start"
                btnFinishRun.visibility = View.VISIBLE
            }
        }else {
            menu?.getItem(0)?.isVisible = true
            binding.apply {
                btnToggleRun.text = "Stop"
                btnFinishRun.visibility = View.GONE
            }
        }
    }

    private fun moveCameraToUser(){
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun addAllPolylines(){
        for (polyline in pathPoints){
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline(){
        if (pathPoints.isNotEmpty() && pathPoints.last().size >1){
            val preLastLatLng = pathPoints.last()[pathPoints.last().size-2] // this gets the second last element of the list
            val lastLatLng = pathPoints.last().last() // This gets the last element of the list and we join last and second last
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    // This sends the intent to our service with the commands attached
    private fun sendCommandToService (action : String) =
        Intent(requireContext(),TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it) // We wont start our service everytime we call this function, instead this function delivers this intent to our service
            // and react to that command
        }

    // Handling the lifecycle of our mapview - we override all the functions of the tracking fragment and associate the mapview lifecycle with it
    override fun onResume() {
        super.onResume()
        binding.mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView?.onDestroy()
    }
// This will help us cache the map, so that while we get the map asynchronously we have something to show
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView?.onSaveInstanceState(outState)
    }
}