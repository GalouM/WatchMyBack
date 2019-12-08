package com.galou.watchmyback.selectCheckListDialog

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
import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.databinding.DialogSelectCheckListBinding
import com.galou.watchmyback.utils.rvAdapter.CheckListAdapter
import com.galou.watchmyback.utils.rvAdapter.CheckListListener

/**
 * A simple [Fragment] subclass.
 */
class SelectCheckListDialog(
    private val checkLists: List<CheckListWithItems>,
    private val listener: CheckListListener
) : DialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterRv: CheckListAdapter

    private lateinit var binding: DialogSelectCheckListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_select_check_list, container, false)
        recyclerView = binding.dialogChecklistsRv
        binding.listener = listener
        binding.lifecycleOwner = this.viewLifecycleOwner

        configureRecyclerView()

        return binding.root
    }

    private fun configureRecyclerView() {
        adapterRv = CheckListAdapter(checkLists, listener)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = adapterRv
        }
    }

}
