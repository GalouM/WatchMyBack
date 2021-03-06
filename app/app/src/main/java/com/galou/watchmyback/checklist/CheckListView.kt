package com.galou.watchmyback.checklist


import android.app.Activity.RESULT_OK
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
import com.galou.watchmyback.addModifyCheckList.AddModifyCheckListActivity
import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.databinding.FragmentCheckListViewBinding
import com.galou.watchmyback.utils.RC_ADD_CHECKLIST
import com.galou.watchmyback.utils.extension.setupSnackBar
import com.galou.watchmyback.utils.extension.visibleOrInvisible
import com.galou.watchmyback.utils.rvAdapter.CheckListAdapter
import com.galou.watchmyback.utils.rvAdapter.CheckListListener
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class CheckListView : Fragment(), CheckListListener {

    private val viewModel: CheckListViewModel by viewModel()
    private lateinit var binding: FragmentCheckListViewBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterRv: CheckListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        configureBinding(inflater, container)
        configureRecyclerView()
        setupObserverViewModel()

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            RC_ADD_CHECKLIST -> {
                if (resultCode == RESULT_OK) viewModel.refresh()
            }
        }
    }

    private fun configureBinding(inflater: LayoutInflater, container: ViewGroup?){
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_check_list_view, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
    }

    private fun configureRecyclerView(){
        recyclerView = binding.checkListViewRv
        adapterRv = CheckListAdapter(listOf(), this)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapterRv
    }

    private fun setupObserverViewModel(){
        setupCheckLists()
        setupOpenModifyCheckList()
        setupSnackBar()
        setupUserConnectedObserver()
    }

    private fun setupSnackBar(){
        val rooView = binding.checkListViewContainer
        rooView.setupSnackBar(this, viewModel.snackbarMessage, Snackbar.LENGTH_LONG)

    }

    private fun setupUserConnectedObserver(){
        viewModel.userLD.observe(this, Observer { if (it != null) fetchCheckList() })
    }

    private fun setupCheckLists(){
        viewModel.checkListLD.observe(this, Observer { updateCheckLists(it) })
    }

    private fun setupOpenModifyCheckList(){
        viewModel.openAddModifyCheckList.observe(this, EventObserver { openAddModifyCheckList() })
    }

    private fun fetchCheckList(){
        viewModel.fetchCheckLists()
    }

    private fun updateCheckLists(checkLists: List<CheckListWithItems>){
        if (checkLists.isNotEmpty()){
            adapterRv.checkLists =  checkLists
            adapterRv.notifyDataSetChanged()
            binding.checklistViewNoChecklist.visibleOrInvisible(false)
        } else binding.checklistViewNoChecklist.visibleOrInvisible(true)

    }

    private fun openAddModifyCheckList(){
        with(Intent(activity!!, AddModifyCheckListActivity::class.java)){
            startActivityForResult(this, RC_ADD_CHECKLIST)
        }
    }

    override fun onClickCheckList(checkList: CheckListWithItems) {
        viewModel.modifyCheckList(checkList)
    }

    override fun onClickCreateCheckList() {}
}
