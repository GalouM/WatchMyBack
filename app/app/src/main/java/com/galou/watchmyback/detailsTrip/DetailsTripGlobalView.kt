package com.galou.watchmyback.detailsTrip


import android.content.Intent
import android.content.Intent.ACTION_CALL
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.EventObserver
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.databinding.FragmentDetailsTripGlobalBinding
import com.galou.watchmyback.utils.rvAdapter.DisplayWatcherAdapter
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class DetailsTripGlobalView : Fragment() {

    private val viewModel: DetailsTripViewModel by sharedViewModel()

    private lateinit var binding: FragmentDetailsTripGlobalBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterRv: DisplayWatcherAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        configureBinding(inflater, container)
        setupObserverViewModel()
        return binding.root
    }

    private fun configureBinding(inflater: LayoutInflater, container: ViewGroup?){
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_details_trip_global, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
    }

    private fun setupObserverViewModel() {
        setupWatcherListObserver()
        setupCallEmergency()
        setupCallTripOwner()

    }

    private fun setupWatcherListObserver(){
        viewModel.tripWatchersLD.observe(this, Observer { displayWatchers(it) })
    }

    private fun setupCallEmergency(){
        viewModel.emergencyNumberLD.observe(this, EventObserver { openPhoneActivity(it) })
    }

    private fun setupCallTripOwner(){
        viewModel.tripOwnerNumberLD.observe(this, EventObserver { openPhoneActivity(it) })
    }

    private fun displayWatchers(watchers: List<User>){
        recyclerView = binding.detailsTripGlobalViewWatchersRv
        adapterRv = DisplayWatcherAdapter(watchers)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapterRv

    }

    private fun openPhoneActivity(number: String){
        with(Intent(ACTION_CALL)){
            data = "tel:$number".toUri()
            startActivity(this)
        }

    }


}
