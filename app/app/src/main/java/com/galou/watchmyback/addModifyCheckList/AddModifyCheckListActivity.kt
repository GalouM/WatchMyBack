package com.galou.watchmyback.addModifyCheckList

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.EventObserver
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.ItemCheckList
import com.galou.watchmyback.data.entity.TripType
import com.galou.watchmyback.databinding.ActivityAddModifyCheckListBinding
import com.galou.watchmyback.selectTripTypeDialog.SelectTripTypeDialog
import com.galou.watchmyback.selectTripTypeDialog.TripTypeSelectionListener
import com.galou.watchmyback.utils.RESULT_CHECKLIST_DELETED
import com.galou.watchmyback.utils.TRIP_TYPE_TAG
import com.galou.watchmyback.utils.extension.setupSnackBar
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel

class AddModifyCheckListActivity : AppCompatActivity(), TripTypeSelectionListener {

    private val viewModel: AddModifyCheckListViewModel by viewModel()
    private lateinit var binding: ActivityAddModifyCheckListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterRv: ModifyItemAdapter

    private var checkListTypeDialog: SelectTripTypeDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureBinding()
        configureToolbar()
        configureRecyclerView()
        setupObserverViewModel()
    }


    private fun setupObserverViewModel() {
        setupSnackBar()
        setupCheckListSaved()
        setupCheckListDeleted()
        setupItemsModifications()
        setupShowTypeDialog()

    }

    private fun configureBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_modify_check_list)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

    }

    private fun setupSnackBar(){
        binding.addModifyChecklistListRoot
            .setupSnackBar(this, viewModel.snackbarMessage, Snackbar.LENGTH_LONG)
    }

    private fun setupCheckListSaved(){
        viewModel.checkListSavedLD.observe(this, Observer { checkListSaved() })
    }

    private fun setupCheckListDeleted(){
        viewModel.checkListDeletedLD.observe(this, Observer { checkListDeleted() })
    }

    private fun setupItemsModifications(){
        viewModel.itemsCheckListLD.observe(this, Observer { updateItemCheckList(it) })
    }

    private fun setupShowTypeDialog(){
        viewModel.typesCheckList.observe(this, EventObserver { showTypeCheckListDialog(it) })
    }

    private fun checkListSaved(){
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun checkListDeleted(){
        setResult(RESULT_CHECKLIST_DELETED)
        finish()
    }

    private fun updateItemCheckList(items: List<ItemCheckList>){
        adapterRv.items = items
        adapterRv.notifyDataSetChanged()
    }

    private fun showTypeCheckListDialog(types: List<TripType>){
        checkListTypeDialog = SelectTripTypeDialog(types, this).apply {
            show(supportFragmentManager, TRIP_TYPE_TAG)
        }


    }

    //-------------------------
    //  CONFIGURE UI
    //-------------------------

    private fun configureRecyclerView() {
        recyclerView = binding.addModifyChecklistRv
        adapterRv = ModifyItemAdapter(listOf(), viewModel)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapterRv
    }

    private fun configureToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.apply {
            setHomeAsUpIndicator(R.drawable.icon_close)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.validate_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.validate_menu_validate -> viewModel.saveCheckList()
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

    //-------------------------
    //  CLICK LISTENER
    //-------------------------
    override fun onClickType(type: TripType) {
        viewModel.selectCheckListType(type)
        checkListTypeDialog?.dismiss()
    }
}
