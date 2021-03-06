package com.galou.watchmyback.tripsView


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.EventObserver
import com.galou.watchmyback.R
import com.galou.watchmyback.data.applicationUse.TripDisplay
import com.galou.watchmyback.databinding.FragmentTripsViewBinding
import com.galou.watchmyback.detailsTrip.DetailsTripActivity
import com.galou.watchmyback.utils.TRIP_ID
import com.galou.watchmyback.utils.extension.setupSnackBar
import com.galou.watchmyback.utils.extension.visibleOrInvisible
import com.galou.watchmyback.utils.rvAdapter.DisplayTripAdapter
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class TripsView : Fragment() {

    private val viewModel: TripsViewModel by viewModel()

    private lateinit var binding: FragmentTripsViewBinding
    private lateinit var recyclerViewTrips: RecyclerView
    private lateinit var adapterRVTrips: DisplayTripAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        configureBinding(inflater, container)
        configureRecyclerView()
        setupViewModelObserver()

        return binding.root
    }

    private fun configureBinding(inflater: LayoutInflater, container: ViewGroup?){
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_trips_view, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.viewmodel = viewModel
    }

    private fun configureRecyclerView(){
        adapterRVTrips = DisplayTripAdapter(listOf(), viewModel)
        recyclerViewTrips = binding.tripsViewRv
        recyclerViewTrips.layoutManager = LinearLayoutManager(activity)
        recyclerViewTrips.adapter = adapterRVTrips

    }

    private fun setupViewModelObserver(){
        setupTripsObserver()
        setupOpenDetailsObserver()
        setupUsrConnectedObserver()
        setupSnackBar()
    }

    private fun setupUsrConnectedObserver(){
        viewModel.userLD.observe(this, Observer { if (it != null) fetchTrips() })
    }

    private fun setupTripsObserver(){
        viewModel.tripsLD.observe(this, Observer { displayTrips(it) })
    }

    private fun setupOpenDetailsObserver(){
        viewModel.tripSelectedLD.observe(this, EventObserver { openTripDetails(it) })
    }

    private fun fetchTrips(){
        viewModel.fetchTripsWatching()
    }

    private fun setupSnackBar(){
        val rooView = binding.tripViewContainer
        rooView.setupSnackBar(this, viewModel.snackbarMessage, Snackbar.LENGTH_LONG)

    }

    private fun displayTrips(trips: List<TripDisplay>){
        if (trips.isNotEmpty()){
            adapterRVTrips.trips = trips
            adapterRVTrips.notifyDataSetChanged()
            binding.tripsViewNoTrips.visibleOrInvisible(false)
        } else binding.tripsViewNoTrips.visibleOrInvisible(true)

    }

    private fun openTripDetails(tripId: String){
        with(Intent(activity!!, DetailsTripActivity::class.java)){
            putExtra(TRIP_ID, tripId)
            startActivity(this)
        }
    }


}
