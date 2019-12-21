package com.galou.watchmyback.addTrip

import android.app.Activity
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
import com.galou.watchmyback.data.applicationUse.Watcher
import com.galou.watchmyback.data.entity.*
import com.galou.watchmyback.databinding.ActivityAddTripBinding
import com.galou.watchmyback.mapPickLocation.PickLocationActivity
import com.galou.watchmyback.selectCheckListDialog.SelectCheckListDialog
import com.galou.watchmyback.selectTripTypeDialog.SelectTripTypeDialog
import com.galou.watchmyback.selectUpdateFrequencyDialog.SelectTripUpdateFrequencyDialog
import com.galou.watchmyback.selectWatcherDialog.SelectWatchersDialog
import com.galou.watchmyback.utils.*
import com.galou.watchmyback.utils.extension.setupSnackBar
import com.galou.watchmyback.utils.rvAdapter.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

class AddTripActivity : AppCompatActivity(),
    TripTypeSelectionListener, UpdateFrequencyListener, CheckListListener, WatcherListener,
    EasyPermissions.PermissionCallbacks
{

    private val viewModel: AddTripViewModel by viewModel()
    private lateinit var binding: ActivityAddTripBinding

    private lateinit var recyclerViewItems: RecyclerView
    private lateinit var adapterItems: SelectItemCheckListAdapter
    private lateinit var recyclerViewStagePoint: RecyclerView
    private lateinit var adapterStagePoint: StagePointAdapter

    private var tripTypeDialog: SelectTripTypeDialog? = null
    private var updateFrequencyDialog: SelectTripUpdateFrequencyDialog? = null
    private var checkListDialog: SelectCheckListDialog? = null
    private var watcherDialog: SelectWatchersDialog? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        configureBinding()
        configureToolbar()
        configureRecyclerViewItem()
        configureRecyclerViewStagePoint()
        setupObserverViewModel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            RC_PICK_LOCATION_MAP -> {
                data?.let {
                    val latitude = data.getDoubleExtra(POINT_LATITUDE, 0.0)
                    val longitude = data.getDoubleExtra(POINT_LONGITUDE, 0.0)
                    viewModel.setPointLocation(latitude, longitude)
                }
            }
        }
    }

    private fun configureBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_trip)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

    }

    //-------------------------
    //  SETUP OBSERVERS
    //-------------------------

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
        setupFetchCurrentLocation()
        setupTripSaved()
        setupOpenMapPicker()

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

    private fun setupFetchCurrentLocation(){
        viewModel.fetchCurrentLocationLD.observe(this, EventObserver { connectLocationService() })
    }

    private fun setupTripSaved(){
        viewModel.tripSavedLD.observe(this, EventObserver { tripCreated() })
    }

    private fun setupOpenMapPicker(){
        viewModel.openMapLD.observe(this, EventObserver {showMapPicker()})
    }


    //-------------------------
    //  DIALOGS
    //-------------------------

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

    private fun showWatcherDialog(watchers: List<Watcher>){
        watcherDialog = SelectWatchersDialog(watchers, this).apply {
            show(supportFragmentManager, WATCHER_DIALOG)
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

    //-------------------------
    //  RECYCLERVIEWS
    //-------------------------

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
    //
    //-------------------------

    private fun connectLocationService(){
        when(isGPSAvailable(this)){
            true -> if(requestPermissionLocation(this)){
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    viewModel.setPointLocation(location.latitude, location.longitude)
                }
            }
            false -> viewModel.gpsNotAvailable()
        }

    }

    //-------------------------
    //  NAVIGATE OTHER ACTIVITIES
    //-------------------------

    private fun showAddCheckListActivity(){
        with(Intent(this, AddModifyCheckListActivity::class.java)){
            startActivity(this)
        }
    }

    private fun showMapPicker(){
        with(Intent(this, PickLocationActivity::class.java)){
            startActivityForResult(this, RC_PICK_LOCATION_MAP)
        }
    }

    private fun showAddFriendsActivity(){
        watcherDialog?.dismiss()
        with(Intent(this, AddFriendActivity::class.java)){
            startActivity(this)
        }
    }

    //-------------------------
    //  TRIP CREATION
    //-------------------------

    private fun tripCreated(){
        setResult(Activity.RESULT_OK)
        finish()
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
        adapterItems = SelectItemCheckListAdapter(listOf())
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

    //-------------------------
    //  PERMISSION CALLBACK
    //-------------------------

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        when(requestCode){
            RC_LOCATION_PERMS -> connectLocationService()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}
}
