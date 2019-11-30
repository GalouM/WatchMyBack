package com.galou.watchmyback.tripMapView


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.galou.watchmyback.AddTripActivity
import com.galou.watchmyback.EventObserver
import com.galou.watchmyback.R
import com.galou.watchmyback.databinding.FragmentMapViewBinding
import com.galou.watchmyback.utils.extension.setupSnackBar
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class TripMapView : Fragment() {

    private val viewModel: TripMapViewModel by viewModel()
    private lateinit var binding: FragmentMapViewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        configureBinding(inflater, container)
        setupObserverViewModel()
        return binding.root
    }

    private fun configureBinding(inflater: LayoutInflater, container: ViewGroup?){
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map_view, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
    }

    private fun setupObserverViewModel(){
        setupSnackBar()
        setupOpenStartNewTrip()

    }

    private fun setupSnackBar(){
        val rooView = binding.mapViewContainer
        rooView.setupSnackBar(this, viewModel.snackbarMessage, Snackbar.LENGTH_LONG)

    }

    private fun setupOpenStartNewTrip(){
        viewModel.openAddTripActivity.observe(this, EventObserver {openStartNewTripActivity() })
    }

    private fun openStartNewTripActivity(){
        with(Intent(activity!!, AddTripActivity::class.java)){
            startActivity(this)
        }
    }


}
