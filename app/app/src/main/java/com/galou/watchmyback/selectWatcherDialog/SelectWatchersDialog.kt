package com.galou.watchmyback.selectWatcherDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.Watcher
import com.galou.watchmyback.databinding.DialogSelectWatcherBinding
import com.galou.watchmyback.utils.rvAdapter.SelectWatcherAdapter
import com.galou.watchmyback.utils.rvAdapter.WatcherListener

/**
 * A simple [Fragment] subclass.
 */
class SelectWatchersDialog(
    private val watchers: List<Watcher>,
    private val listener: WatcherListener
) : DialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterRv: SelectWatcherAdapter

    private lateinit var binding: DialogSelectWatcherBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_select_watcher, container, false)
        recyclerView = binding.dialogChecklistsRv
        binding.listener = listener
        binding.lifecycleOwner = this.viewLifecycleOwner

        configureRecyclerView()

        return binding.root
    }

    private fun configureRecyclerView() {
        adapterRv = SelectWatcherAdapter(watchers, listener)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = adapterRv
        }
    }

}
