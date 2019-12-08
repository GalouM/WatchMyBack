package com.galou.watchmyback.selectTripTypeDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.TripType
import com.galou.watchmyback.utils.rvAdapter.SelectTripTypeAdapter
import com.galou.watchmyback.utils.rvAdapter.TripTypeSelectionListener

/**
 * @author galou
 * 2019-11-24
 */
class SelectTripTypeDialog(
    private val types: List<TripType>,
    private val clickListener: TripTypeSelectionListener
) : DialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterRv: SelectTripTypeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.dialog_select_trip_type, container, false)
        recyclerView = view.findViewById(R.id.dialog_trip_type_rv)
        configureRecyclerView()

        return view
    }

    private fun configureRecyclerView() {
        adapterRv = SelectTripTypeAdapter(types, clickListener)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = adapterRv
        }
    }
}