package com.galou.watchmyback.addTrip

import android.app.TimePickerDialog
import android.content.Intent
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
import com.galou.watchmyback.addFriend.AddFriendActivity
import com.galou.watchmyback.addModifyCheckList.AddModifyCheckListActivity
import com.galou.watchmyback.data.entity.*
import com.galou.watchmyback.databinding.ActivityAddTripBinding
import com.galou.watchmyback.selectCheckListDialog.SelectCheckListDialog
import com.galou.watchmyback.selectTripTypeDialog.SelectTripTypeDialog
import com.galou.watchmyback.selectUpdateFrequencyDialog.SelectTripUpdateFrequencyDialog
import com.galou.watchmyback.selectWatcherDialog.SelectWatchersDialog
import com.galou.watchmyback.utils.CHECKLIST_DIALOG
import com.galou.watchmyback.utils.TRIP_TYPE_TAG
import com.galou.watchmyback.utils.UPDATE_HZ_TAG
import com.galou.watchmyback.utils.WATCHER_DIALOG
import com.galou.watchmyback.utils.extension.setupSnackBar
import com.galou.watchmyback.utils.rvAdapter.CheckListListener
import com.galou.watchmyback.utils.rvAdapter.TripTypeSelectionListener
import com.galou.watchmyback.utils.rvAdapter.UpdateFrequencyListener
import com.galou.watchmyback.utils.rvAdapter.WatcherListener
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class AddTripActivity : AppCompatActivity(),
    TripTypeSelectionListener, UpdateFrequencyListener, CheckListListener,
        WatcherListener
{

    private val viewModel: AddTripViewModel by viewModel()
    private lateinit var binding: ActivityAddTripBinding

    private lateinit var recyclerViewItems: RecyclerView
    private lateinit var adapterItems: ItemCheckListAdapter
    private lateinit var recyclerViewStagePoint: RecyclerView
    private lateinit var adapterStagePoint: StagePointAdapter

    private var tripTypeDialog: SelectTripTypeDialog? = null
    private var updateFrequencyDialog: SelectTripUpdateFrequencyDialog? = null
    private var checkListDialog: SelectCheckListDialog? = null
    private var watcherDialog: SelectWatchersDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureBinding()
        configureToolbar()
        configureRecyclerViewItem()
        configureRecyclerViewStagePoint()
        setupObserverViewModel()
    }

    private fun setupObserverViewModel() {
        setupSnackBar()
        setupShowTypeDialog()
        setupShowUpdateFrequenciesDialog()
        setupShowCheckListDialog()
        setupOpenCreateCheckList()
        setupOpenWatcherDialog()
        setupOpenAddFriendsActivity()
        setupItemChecklist()
        setupAddStagePoint()
        setupOpenTimePicker()

    }

    private fun configureBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_trip)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

    }

    private fun setupSnackBar(){
        binding.addTripViewRoot
            .setupSnackBar(this, viewModel.snackbarMessage, Snackbar.LENGTH_LONG)
    }

    private fun setupShowTypeDialog(){
        viewModel.typesTrip.observe(this, EventObserver { showTripTypeDialog(it) })
    }

    private fun setupShowUpdateFrequenciesDialog(){
        viewModel.updateHzTripLD.observe(this, EventObserver { showUpdateFrequenciesDialog(it) })
    }

    private fun setupShowCheckListDialog(){
        viewModel.openPickCheckListLD.observe(this, EventObserver { showCheckListDialog(it) })
    }

    private fun setupOpenCreateCheckList(){
        viewModel.openAddCheckListLD.observe(this, EventObserver { showAddCheckListActivity() })
    }

    private fun setupOpenWatcherDialog(){
        viewModel.friendsLD.observe(this, EventObserver { showWatcherDialog(it) })
    }

    private fun setupOpenAddFriendsActivity(){
        viewModel.openAddFriendLD.observe(this, EventObserver { showAddFriendsActivity() })
    }

    private fun setupItemChecklist(){
        viewModel.itemCheckListLD.observe(this, Observer { updateItemsCheckList(it) })
    }

    private fun setupAddStagePoint(){
        viewModel.stagePointsLD.observe(this, Observer { updateStagePoint(it) })
    }

    private fun setupOpenTimePicker(){
        viewModel.openTimePickerLD.observe(this, EventObserver { showTimePicker(it) })
    }

    private fun showTripTypeDialog(types: List<TripType>){
        tripTypeDialog = SelectTripTypeDialog(types, this).apply {
            show(supportFragmentManager, TRIP_TYPE_TAG)
        }
    }

    private fun showUpdateFrequenciesDialog(frequencies: List<TripUpdateFrequency>){
        updateFrequencyDialog = SelectTripUpdateFrequencyDialog(frequencies, this).apply {
            show(supportFragmentManager, UPDATE_HZ_TAG)
        }
    }

    private fun showCheckListDialog(checkList: List<CheckListWithItems>){
        checkListDialog = SelectCheckListDialog(checkList, this).apply {
            show(supportFragmentManager, CHECKLIST_DIALOG)
        }
    }

    private fun showAddCheckListActivity(){
        with(Intent(this, AddModifyCheckListActivity::class.java)){
            startActivity(this)
        }
    }

    private fun showWatcherDialog(watchers: List<Watcher>){
        watcherDialog = SelectWatchersDialog(watchers, this).apply {
            show(supportFragmentManager, WATCHER_DIALOG)
        }
    }

    private fun showAddFriendsActivity(){
        watcherDialog?.dismiss()
        with(Intent(this, AddFriendActivity::class.java)){
            startActivity(this)
        }
    }

    private fun showTimePicker(data: Map<TimeDisplay, PointTripWithData>){
        val point = data.values.first()
        val date = Calendar.getInstance()
        point.pointTrip.time?.let {
            date.time = it
            val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            date.set(Calendar.DAY_OF_YEAR, currentDay)
        }
        val currentHour = date.get(Calendar.HOUR_OF_DAY)
        val currentMinutes = date.get(Calendar.MINUTE)
        val is24hourFormat = when(data.keys.first()){
            TimeDisplay.H_24 -> true
            TimeDisplay.H_12 -> false
        }

        val timeSetListener = TimePickerDialog.OnTimeSetListener{ _, hour, minutes ->
            with(date){
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minutes)
            }
            viewModel.setTimeSchedulePoint(point, date)
        }

        TimePickerDialog(
            this, R.style.MyTimePickerDialogTheme, timeSetListener, currentHour, currentMinutes, is24hourFormat
        ).show()

    }

    private fun updateItemsCheckList(items: List<ItemCheckList>?){
        items?.let{
            with(adapterItems){
                this.items = items
                notifyDataSetChanged()
            }
        }

    }

    private fun updateStagePoint(points: List<PointTripWithData>){
        with(adapterStagePoint){
            this.points = points
            notifyDataSetChanged()

        }
    }

    //-------------------------
    //  CONFIGURE UI
    //-------------------------
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
            R.id.validate_menu_validate -> viewModel.startTrip()
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun configureRecyclerViewItem(){
        recyclerViewItems = binding.addTripChecklistItemRv
        adapterItems = ItemCheckListAdapter(listOf(), viewModel)
        recyclerViewItems.layoutManager = LinearLayoutManager(this)
        recyclerViewItems.adapter = adapterItems
    }

    private fun configureRecyclerViewStagePoint(){
        recyclerViewStagePoint = binding.addTripStagesPointsRv
        adapterStagePoint = StagePointAdapter(listOf(), viewModel)
        recyclerViewStagePoint.layoutManager = LinearLayoutManager(this)
        recyclerViewStagePoint.adapter = adapterStagePoint
    }

    //-------------------------
    //  CLICK LISTENER
    //-------------------------
    override fun onClickType(type: TripType) {
        viewModel.selectTripType(type)
        tripTypeDialog?.dismiss()
    }

    override fun onClickFrequency(frequency: TripUpdateFrequency) {
        viewModel.selectUpdateFrequency(frequency)
        updateFrequencyDialog?.dismiss()
    }

    override fun onClickCheckList(checkList: CheckListWithItems) {
        viewModel.selectCheckList(checkList)
        checkListDialog?.dismiss()
    }

    override fun onClickCreateCheckList() {
        checkListDialog?.dismiss()
        viewModel.openAddCheckListActivity()
    }

    override fun onClickWatcher(watcher: Watcher) {
        viewModel.addRemoveWatcher(watcher)
    }

    override fun onClickAddFriends() {
        viewModel.openAddFriendsActivity()
    }
}
