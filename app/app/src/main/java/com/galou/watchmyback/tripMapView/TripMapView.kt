package com.galou.watchmyback.tripMapView


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.galou.watchmyback.BuildConfig
import com.galou.watchmyback.EventObserver
import com.galou.watchmyback.R
import com.galou.watchmyback.addTrip.AddTripActivity
import com.galou.watchmyback.data.applicationUse.Coordinate
import com.galou.watchmyback.databinding.FragmentMapViewBinding
import com.galou.watchmyback.detailsPoint.DetailsPointActivity
import com.galou.watchmyback.utils.*
import com.galou.watchmyback.utils.extension.addIconLocationAccent
import com.galou.watchmyback.utils.extension.addIconLocationPrimary
import com.galou.watchmyback.utils.extension.setupSnackBar
import com.google.android.material.snackbar.Snackbar
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class TripMapView : Fragment() {

    private val viewModel: TripMapViewModel by viewModel()
    private lateinit var binding: FragmentMapViewBinding

    private lateinit var mapView: MapView
    private lateinit var mapBox: MapboxMap
    private lateinit var symbolManager: SymbolManager
    private lateinit var styleMap: Style

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Mapbox.getInstance(activity!!, BuildConfig.MapSDKKey)

        configureBinding(inflater, container)
        setupObserverViewModel()
        mapView.onCreate(savedInstanceState)
        return binding.root
    }

    private fun configureBinding(inflater: LayoutInflater, container: ViewGroup?){
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map_view, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        mapView = binding.tripMapViewMap
    }

    private fun setupObserverViewModel(){
        setupSnackBar()
        setupOpenStartNewTrip()
        setupCenterCameraObserver()
        setupUserConnected()
        setupStartPointObserver()
        setupEndPointObserver()
        setupSchedulePointsObserver()
        setupCheckUpPointsObserver()
        setupShowDetailPointObserver()

    }

    private fun setupSnackBar(){
        val rooView = binding.mapViewContainer
        rooView.setupSnackBar(this, viewModel.snackbarMessage, Snackbar.LENGTH_LONG)

    }

    private fun setupOpenStartNewTrip(){
        viewModel.openAddTripActivity.observe(this, EventObserver {openStartNewTripActivity() })
    }

    private fun setupCenterCameraObserver(){
        viewModel.centerCameraUserLD.observe(this, EventObserver { centerCameraOnUser() })
    }

    private fun setupUserConnected(){
        viewModel.userLD.observe(this, Observer { setupMap() })
    }
    private fun setupStartPointObserver(){
        viewModel.startPointLD.observe(this, Observer { displaySchedulePoints(it) })
    }

    private fun setupEndPointObserver(){
        viewModel.endPointLD.observe(this, Observer { displaySchedulePoints(it) })
    }

    private fun setupSchedulePointsObserver(){
        viewModel.schedulePointsLD.observe(this, Observer { displaySchedulePoints(it) })
    }

    private fun setupCheckUpPointsObserver(){
        viewModel.checkedPointsLD.observe(this, Observer { displayCheckedUpPoint(it) })
    }

    private fun setupShowDetailPointObserver(){
        viewModel.showPointDetailsLD.observe(this, EventObserver { showPointDetails() })
    }

    private fun openStartNewTripActivity(){
        with(Intent(activity!!, AddTripActivity::class.java)){
            startActivity(this)
        }
    }

    private fun setupMap(){
        mapView.getMapAsync { mapboxMap ->
            mapBox = mapboxMap
            mapBox.setStyle(Style.SATELLITE) {style ->
                styleMap = style
                styleMap.addIconLocationAccent(activity!!)
                styleMap.addIconLocationPrimary(activity!!)
                symbolManager = SymbolManager(mapView, mapBox, styleMap).apply {
                    iconAllowOverlap = true
                    iconPadding = 0.1f
                }
                displayUserLocation()
                viewModel.fetchAndDisplayUserActiveTrip()

            }

        }
    }

    private fun centerCameraOnUser(){
        displayUserLocation()
    }

    private fun displayUserLocation() {
        if(requestPermissionLocation(activity!!) && isGPSAvailable(activity!!)) {
            with(mapBox.locationComponent) {
                activateLocationComponent(
                    LocationComponentActivationOptions.builder(
                        activity!!.applicationContext,
                        styleMap
                    ).build()
                )
                isLocationComponentEnabled = true
                cameraMode = CameraMode.TRACKING
                renderMode = RenderMode.COMPASS
                zoomWhileTracking(15.0)
            }
        } else {
            viewModel.gpsNotAvailable()
        }
    }

    private fun displaySchedulePoints(pointData: Map<String, Coordinate>){
        for((id, coordinate) in pointData){
            displayPointPrimaryColor(id, coordinate)
        }
    }

    private fun displayCheckedUpPoint(pointData: Map<String, Coordinate>){
        for((id, coordinate) in pointData){
            displayPointAccentColor(id, coordinate)
        }
    }

    private fun displayPointAccentColor(pointId: String, coordinate: Coordinate){
        symbolManager.create(SymbolOptions()
            .withLatLng(LatLng(coordinate.latitude, coordinate.longitude))
            .withIconImage(ICON_LOCATION_ACCENT)
            .withIconSize(ICON_MAP_SIZE)
            .withIconOffset(ICON_MAP_OFFSET)
            .withTextField(pointId)
        )
    }

    private fun displayPointPrimaryColor(pointId: String, coordinate: Coordinate){
        symbolManager.create(SymbolOptions()
            .withLatLng(LatLng(coordinate.latitude, coordinate.longitude))
            .withIconImage(ICON_LOCATION_PRIMARY)
            .withIconSize(ICON_MAP_SIZE)
            .withIconOffset(ICON_MAP_OFFSET)
            .withTextField(pointId)
        )
    }

    private fun showPointDetails(){
        with(Intent(activity!!, DetailsPointActivity::class.java)){
            startActivity(this)
        }
    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


}
