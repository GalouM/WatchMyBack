package com.galou.watchmyback.mapPickLocation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.galou.watchmyback.BuildConfig
import com.galou.watchmyback.EventObserver
import com.galou.watchmyback.R
import com.galou.watchmyback.data.applicationUse.Coordinate
import com.galou.watchmyback.databinding.ActivityPickLocationBinding
import com.galou.watchmyback.utils.*
import com.galou.watchmyback.utils.extension.addIconLocationAccent
import com.galou.watchmyback.utils.extension.addIconLocationPrimary
import com.galou.watchmyback.utils.extension.setupSnackBar
import com.google.android.material.snackbar.Snackbar
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import org.koin.android.viewmodel.ext.android.viewModel

class PickLocationActivity : AppCompatActivity(), MapboxMap.OnMapLongClickListener{

    private lateinit var mapView: MapView
    private lateinit var mapBox: MapboxMap
    private lateinit var symbolManager: SymbolManager
    private lateinit var styleMap: Style

    private var pointSelected: Symbol? = null
    private var pointsTrip: MutableList<Symbol>? = null

    private val viewModel: PickLocationMapViewModel by viewModel()
    private lateinit var binding: ActivityPickLocationBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, BuildConfig.MapSDKKey)

        configureBinding()
        configureToolbar()
        mapView = findViewById(R.id.pick_location_view_map)
        mapView.onCreate(savedInstanceState)

        setupMap()
        setupObservers()

    }

    private fun configureBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pick_location)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

    }

    private fun setupObservers(){
        setupPointSelectedObserver()
        setupPointsTripObserver()
        setupCenterCameraObserver()
        setupSnackBar()
        setupValidateObserver()
    }

    private fun setupPointSelectedObserver(){
        viewModel.pointSelectedLocation.observe(this, EventObserver { displayPointSelected(it) })
    }

    private fun setupPointsTripObserver(){
        viewModel.pointsTripLocationLD.observe(this, Observer { displayPointsTrip(it) })
    }

    private fun setupCenterCameraObserver(){
        viewModel.centerCameraLD.observe(this, EventObserver { centerCameraOnUser() })
    }

    private fun setupSnackBar(){
        binding.pickLocationViewRoot
            .setupSnackBar(this, viewModel.snackbarMessage, Snackbar.LENGTH_LONG)
    }

    private fun setupValidateObserver(){
        viewModel.validateNewCoordinateLD.observe(this, EventObserver { validatePointCoordinate(it) })
    }

    private fun displayPointSelected(coordinate: Coordinate){
        displayPointLocation(coordinate.latitude, coordinate.longitude)
        mapBox.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(coordinate.latitude, coordinate.longitude), 15.0))


    }

    private fun displayPointsTrip(coordinate: List<Coordinate>){
        pointsTrip?.let { symbolManager.delete(pointsTrip) }
        pointsTrip = mutableListOf()
        coordinate.forEach { location ->
            val symbol = symbolManager.create(SymbolOptions()
                .withLatLng(LatLng(location.latitude, location.longitude))
                .withIconImage(ICON_LOCATION_PRIMARY)
                .withIconSize(1.3f)
            )
            pointsTrip?.add(symbol)

        }
    }

    private fun validatePointCoordinate(coordinate: Coordinate){
        val intent = Intent().apply{
            putExtra(POINT_LATITUDE, coordinate.latitude)
            putExtra(POINT_LONGITUDE, coordinate.longitude)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun setupMap(){
        mapView.getMapAsync { mapboxMap ->
            mapBox = mapboxMap
            mapBox.setStyle(Style.SATELLITE) {style ->
                styleMap = style
                styleMap.addIconLocationAccent(this)
                styleMap.addIconLocationPrimary(this)
                symbolManager = SymbolManager(mapView, mapBox, styleMap)
                displayUserLocation()
                viewModel.onMapReady()

            }
            mapBox.addOnMapLongClickListener(this)

        }
    }

    private fun displayUserLocation() {
        if(requestPermissionLocation(this) && isGPSAvailable(this)) {
            with(mapBox.locationComponent) {
                activateLocationComponent(
                    LocationComponentActivationOptions.builder(
                        applicationContext,
                        styleMap
                    ).build()
                )
                isLocationComponentEnabled = true
                cameraMode = CameraMode.TRACKING
                renderMode = RenderMode.COMPASS
                zoomWhileTracking(15.0)
            }
        }
    }

    private fun centerCameraOnUser(){
        displayUserLocation()
    }


    override fun onMapLongClick(point: LatLng): Boolean {
        viewModel.updatePointPosition(point.latitude, point.longitude)
        return true
    }

    private fun displayPointLocation(latitude: Double, longitude: Double){
        pointSelected?.let { symbolManager.delete(it) }
        pointSelected = symbolManager.create(SymbolOptions()
            .withLatLng(LatLng(latitude, longitude))
            .withIconImage(ICON_LOCATION_ACCENT)
            .withIconSize(1.3f)
            .withIconOffset(floatArrayOf(0f, -8f).toTypedArray())
        )
    }

    //-------------------------
    //  CONFIGURE UI
    //-------------------------
    private fun configureToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.validate_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.validate_menu_validate -> viewModel.validateCoordinate()
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
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

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        mapView.onSaveInstanceState(outState)
    }
}
