package com.galou.watchmyback.selectUpdateFrequencyDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.TripUpdateFrequency
import com.galou.watchmyback.utils.rvAdapter.SelectFrequencyAdapter
import com.galou.watchmyback.utils.rvAdapter.UpdateFrequencyListener

/**
 * A simple [Fragment] subclass.
 * Use the [SelectTripUpdateFrequencyDialog.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelectTripUpdateFrequencyDialog(
    private val updateFrequency: List<TripUpdateFrequency>,
    private val listener: UpdateFrequencyListener
) : DialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterRv: SelectFrequencyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.dialog_select_trip_update_frequency, container, false)
        recyclerView = view.findViewById(R.id.dialog_update_frequency_rv)
        configureRecyclerView()

        return view
    }

    private fun configureRecyclerView() {
        adapterRv = SelectFrequencyAdapter(
            updateFrequency,
            listener
        )
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = adapterRv
        }

    }
}
