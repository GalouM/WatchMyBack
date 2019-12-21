package com.galou.watchmyback.detailsTrip


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.ItemCheckList
import com.galou.watchmyback.databinding.FragmentDetailsTripCheckListBinding
import com.galou.watchmyback.utils.rvAdapter.DisplayItemCheckListAdapter
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class DetailsTripCheckListView : Fragment() {

    private val viewModel: DetailsTripViewModel by sharedViewModel()

    private lateinit var binding: FragmentDetailsTripCheckListBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        configureBinding(inflater, container)
        setupViewModelObserver()

        return binding.root
    }

    private fun configureBinding(inflater: LayoutInflater, container: ViewGroup?){
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_details_trip_check_list, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
    }

    private fun setupViewModelObserver(){
        setupItemObserver()
    }

    private fun setupItemObserver(){
        viewModel.itemsCheckListLD.observe(this, Observer { displayCheckListItems(it) })
    }

    private fun displayCheckListItems(items: List<ItemCheckList>){
        val recyclerView = binding.detailsTripChecklistRv
        val adapter = DisplayItemCheckListAdapter(items)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
    }


}
