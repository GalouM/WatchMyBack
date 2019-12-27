package com.galou.watchmyback.addTrip

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.galou.watchmyback.Event
import com.galou.watchmyback.R
import com.galou.watchmyback.backgroundWork.CheckUpWorker
import com.galou.watchmyback.base.BaseViewModel
import com.galou.watchmyback.data.applicationUse.Watcher
import com.galou.watchmyback.data.entity.*
import com.galou.watchmyback.data.repository.CheckListRepository
import com.galou.watchmyback.data.repository.FriendRepository
import com.galou.watchmyback.data.repository.TripRepository
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.utils.CHECK_UP_WORKER_TAG
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.USER_ID_DATA
import com.galou.watchmyback.utils.extension.emitNewValue
import com.galou.watchmyback.utils.extension.filterOrCreateMainPoint
import com.galou.watchmyback.utils.extension.filterScheduleStage
import com.galou.watchmyback.utils.extension.toWatcher
import com.galou.watchmyback.utils.todaysDate
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * ViewModel for [AddTripActivity]
 * 
 * Inherit from [BaseViewModel]
 *
 * @property friendRepository [FriendRepository] reference
 * @property userRepository [UserRepository] reference
 * @property checkListRepository [CheckListRepository] reference
 * @property tripRepository [TripRepository] reference
 */

class AddTripViewModel(
    private val friendRepository: FriendRepository,
    val userRepository: UserRepository,
    private val checkListRepository: CheckListRepository,
    private val tripRepository: TripRepository
) : BaseViewModel(){

    private val currentUser = userRepository.currentUser.value ?: throw IllegalAccessException("No user set")

    private val trip = TripWithData(
        trip = Trip(
            userId = currentUser.id,
            status = TripStatus.ON_GOING,
            active = true
        ),
        points = mutableListOf(),
        watchers = mutableListOf()
    )

    private var checkList: CheckListWithItems? = null

    val preferences: LiveData<UserPreferences> = userRepository.userPreferences

    private val tripeType = TripType.values().toList()
    private val tripUpdateFrequency = TripUpdateFrequency.values().toList()

    private val _tripLD = MutableLiveData<Trip>()
        get() = field.apply { value = trip.trip }
    val tripLD: LiveData<Trip> = _tripLD

    private val _typesTrip = MutableLiveData<Event<List<TripType>>>()
    val typesTrip: LiveData<Event<List<TripType>>> = _typesTrip

    private val _updateHzTripLD = MutableLiveData<Event<List<TripUpdateFrequency>>>()
    val updateHzTripLD: LiveData<Event<List<TripUpdateFrequency>>> = _updateHzTripLD

    private val _watchersLD = MutableLiveData<List<User>>()
        get() = field.apply { value = trip.watchers }
    val watchersLD: LiveData<List<User>> = _watchersLD

    private val _friendsLD = MutableLiveData<Event<List<Watcher>>>()
    val friendsLD: LiveData <Event<List<Watcher>>> = _friendsLD

    private val _itemsCheckListLD = MutableLiveData<List<ItemCheckList>>()
        get() = field.apply { value = checkList?.items }
    val itemCheckListLD: LiveData<List<ItemCheckList>> = _itemsCheckListLD

    private val _openPickCheckListLD = MutableLiveData<Event<List<CheckListWithItems>>>()
    val openPickCheckListLD: LiveData<Event<List<CheckListWithItems>>> = _openPickCheckListLD

    private val _openTimePickerLD = MutableLiveData<Event<Map<TimeDisplay, PointTripWithData>>>()
    val openTimePickerLD: LiveData<Event<Map<TimeDisplay, PointTripWithData>>> = _openTimePickerLD

    private val _openAddCheckListLD = MutableLiveData<Event<Unit>>()
    val openAddCheckListLD: LiveData<Event<Unit>> = _openAddCheckListLD

    private val _openAddFriendLD = MutableLiveData<Event<Unit>>()
    val openAddFriendLD: LiveData<Event<Unit>> = _openAddFriendLD

    private val _startPointLD = MutableLiveData<PointTripWithData>()
        get() {
            if (field.value == null)  field.value = trip.points.filterOrCreateMainPoint(TypePoint.START, trip.trip.id)
            return field
        }
    val startPointLD: LiveData<PointTripWithData> = _startPointLD

    private val _endPointLD = MutableLiveData<PointTripWithData>()
        get() {
            if (field.value == null)  field.value = trip.points.filterOrCreateMainPoint(TypePoint.END, trip.trip.id)
            return field
        }
    val endPointLD: LiveData<PointTripWithData> = _endPointLD

    private val _stagePointsLD = MutableLiveData<MutableList<PointTripWithData>>()
        get() = field.apply { value = trip.points.filterScheduleStage() }
    val stagePointsLD: LiveData<MutableList<PointTripWithData>> = _stagePointsLD

    private val _openMapLD = MutableLiveData<Event<Unit>>()
    val openMapLD: LiveData<Event<Unit>> = _openMapLD

    private val _fetchCurrentLocationLD = MutableLiveData<Event<Unit>>()
    val fetchCurrentLocationLD: LiveData<Event<Unit>> = _fetchCurrentLocationLD
    
    private val _typeError = MutableLiveData<Int?>()
    val typeError: LiveData<Int?> = _typeError

    private val _updateFrequencyError = MutableLiveData<Int?>()
    val updateFrequencyError: LiveData<Int?> = _updateFrequencyError
    
    private val _watcherError = MutableLiveData<Int?>()
    val watcherError: LiveData<Int?> = _watcherError
    
    private val _startPointLatError = MutableLiveData<Int?>()
    val startPointLatError: LiveData<Int?> = _startPointLatError

    private val _startPointLngError = MutableLiveData<Int?>()
    val startPointLngError: LiveData<Int?> = _startPointLngError

    private val _startPointTimeError = MutableLiveData<Int?>()
    val startPointTimeError: LiveData<Int?> = _startPointTimeError

    private val _endPointLatError = MutableLiveData<Int?>()
    val endPointLatError: LiveData<Int?> = _endPointLatError

    private val _endPointLngError = MutableLiveData<Int?>()
    val endPointLngError: LiveData<Int?> = _endPointLngError

    private val _endPointTimeError = MutableLiveData<Int?>()
    val endPointTimeError: LiveData<Int?> = _endPointTimeError

    private val _tripSavedLD = MutableLiveData<Event<Unit>>()
    val tripSavedLD: LiveData<Event<Unit>> = _tripSavedLD


    /**
     * Emit the different type of [TripType] possible
     *
     */
    fun showTripTypeDialog(){
        _typesTrip.value = Event(tripeType)
    }

    /**
     * Emit the different type of [TripUpdateFrequency] possible
     *
     */
    fun showTripUpdateFrequency(){
        _updateHzTripLD.value = Event(tripUpdateFrequency)
    }

    /**
     * Emit a list of the user's friends
     * 
     * @see FriendRepository.fetchUserFriend
     *
     */
    fun showFriendsList(){
        _dataLoading.value = true
        viewModelScope.launch {
            when(val result = friendRepository.fetchUserFriend(currentUser, false)){
                is Result.Success -> _friendsLD.value = Event(result.data.toWatcher(trip.watchers))
                is Result.Error, is Result.Canceled -> showSnackBarMessage(R.string.error_fetch_friends)
            }
            _dataLoading.value = false
        }
    }

    /**
     * Emit all the user's [CheckList] of a specific [TripType]
     * If no trip type has been selected yet it will emit an message
     * 
     * @see CheckListRepository.fetchUserCheckLists
     *
     */
    fun showCheckLists(){
        val tripType = tripLD.value?.type
        if (tripType == null){
            showSnackBarMessage(R.string.select_type_first)
        } else {
            _dataLoading.value = true
            viewModelScope.launch {
                when(val result = checkListRepository.fetchUserCheckLists(currentUser.id, false)){
                    is Result.Success -> {
                        _openPickCheckListLD.value = Event(result.data.filter { it.checkList.tripType == tripType })
                    }
                    is Result.Error, is Result.Canceled -> showSnackBarMessage(R.string.error_fetch_check_lists)
                }
                _dataLoading.value = false
            }
        }

    }

    /**
     * Open the Time Picker dialog
     *
     * @param viewId ID of the view clicked
     *
     * @see getPointFromButtonId
     */
    fun showTimePicker(viewId: Int){
        showTimePicker(getPointFromButtonId(viewId))
    }

    /**
     * Open the time Picker dialog
     *
     * @param point point to update
     */
    fun showTimePicker(point: PointTripWithData){
        _openTimePickerLD.value = Event(mapOf(preferences.value!!.timeDisplay to point))
    }

    /**
     * Open the activity to create a new checklist
     *
     */
    fun openAddCheckListActivity(){
        checkListRepository.checkList = null
        _openAddCheckListLD.value = Event(Unit)
    }

    /**
     * Open the activity to add friends
     *
     */
    fun openAddFriendsActivity(){
        _openAddFriendLD.value = Event(Unit)
    }

    /**
     * Add or remove a watcher to the trip's watchers list and emit the new list of active watchers
     *
     * @param watcher User to dd or remove to the list of watchers
     * 
     * @see Watcher
     */
    fun addRemoveWatcher(watcher: Watcher){
        when(watcher.watchTrip){
            false -> trip.watchers.remove(watcher.user)
            true -> trip.watchers.add(watcher.user)
        }
        _watchersLD.emitNewValue()
    }


    /**
     * Assign the Checklist selected to the trip
     *
     * @param checkListPicked [CheckListWithItems] selected
     */
    fun selectCheckList(checkListPicked: CheckListWithItems){
        checkList = checkListPicked
        trip.trip.checkListId = checkListPicked.checkList.id
        checkList?.items?.forEach { it.checked = false }

        _itemsCheckListLD.emitNewValue()
        _tripLD.emitNewValue()
    }

    /**
     * Assign the [TripUpdateFrequency] selected to the trip
     *
     * @param frequency [TripUpdateFrequency] selected
     */
    fun selectUpdateFrequency(frequency: TripUpdateFrequency){
        trip.trip.updateFrequency = frequency
        _tripLD.emitNewValue()
    }

    /**
     * Assign the [TripType] selected to the trip
     *
     * @param type [TripType] selected
     */
    fun selectTripType(type: TripType){
        trip.trip.type = type
        _tripLD.emitNewValue()
    }
    

    /**
     * Add a point of type [TypePoint.SCHEDULE_STAGE] to the trip
     *
     */
    fun addStagePoint(){
        trip.points.add(PointTripWithData(
            pointTrip = PointTrip(
                typePoint = TypePoint.SCHEDULE_STAGE,
                tripId = trip.trip.id
            )
        ))
        _stagePointsLD.emitNewValue()
    }

    /**
     * Set the point location from the user's current location
     *
     * @param idButton id of the button clicked to get the point type
     * @see getPointFromButtonId
     * @see fetchCurrentLocation
     */
    fun setPointFromCurrentLocation(idButton: Int) {
        _dataLoading.value = true
        fetchCurrentLocation(getPointFromButtonId(idButton))
    }

    /**
     * Set the point location from a map
     *
     * @param idButton id of the button clicked to get the point type
     * @see getPointFromButtonId
     * @see showMapWithMarker
     */
    fun setPointFromMap(idButton: Int) {
        showMapWithMarker(getPointFromButtonId(idButton))
    }

    /**
     * Set the point location from a map
     *
     * @param point Point to to update
     * @see fetchCurrentLocation
     */
    fun setPointFromMap(point: PointTripWithData){
        showMapWithMarker(point)
    }

    /**
     * Set the location of a point
     *
     * @param lat latitude of the point
     * @param lgn longitude of the point
     * @param point point to update
     */
    fun setPointLocation(lat: Double, lgn: Double, point: PointTripWithData){
        point.location!!.apply {
            latitude = lat.toBigDecimal().setScale(4, RoundingMode.HALF_UP).toDouble()
            longitude = lgn.toBigDecimal().setScale(4, RoundingMode.HALF_UP).toDouble()
        }
        emitNewValuePoint(point)
        _dataLoading.value = false
    }

    /**
     * Set the location of the latest selected point
     *
     * @see TripRepository.pointSelected
     *
     * @param lat latitude of the point
     * @param lgn longitude of the point
     */
    fun setPointLocation(lat: Double, lgn: Double){
        tripRepository.pointSelected?.location!!.apply {
            latitude = lat.toBigDecimal().setScale(4, RoundingMode.UP).toDouble()
            longitude = lgn.toBigDecimal().setScale(4, RoundingMode.UP).toDouble()
        }
        emitNewValuePoint(tripRepository.pointSelected ?: throw Exception("no point selected saved in repo"))
        tripRepository.pointSelected = null
        _dataLoading.value = false
    }


    /**
     * Set the time scheduled for a [PointTrip]
     *
     * @param point Point to update
     * @param date date selected
     */
    fun setTimeSchedulePoint(point: PointTripWithData, date: Calendar){
        if (date.time.before(todaysDate)) date.add(Calendar.DATE, 1)
        point.pointTrip.time = date.time
        emitNewValuePoint(point)
    }

    /**
     * Emit the new value of a point
     * Check the type of point and emit the live data accordingly
     *
     * @param point
     */
    private fun emitNewValuePoint(point: PointTripWithData){
        when(point.pointTrip.typePoint){
            TypePoint.START -> _startPointLD.emitNewValue()
            TypePoint.END -> _endPointLD.emitNewValue()
            else -> _stagePointsLD.emitNewValue()

        }
    }

    /**
     * Show message that GPS is off
     *
     */
    fun gpsNotAvailable(){
        showSnackBarMessage(R.string.turn_on_gps)
    }


    /**
     * Emit LiveData to fetch the current location of the user
     *
     * @param point point to update location with
     */
    private fun fetchCurrentLocation(point: PointTripWithData){
        tripRepository.pointSelected = point
        _fetchCurrentLocationLD.value = Event(Unit)
    }

    /**
     * Show a map to set the location of the point
     *
     * @param point to update
     */
    private fun showMapWithMarker(point: PointTripWithData){
        tripRepository.pointSelected = point
        tripRepository.tripPoints = trip.points
        _openMapLD.value = Event(Unit)
    }

    /**
     * Get a point from the view clicked
     *
     * @param idButton Id of the view clicked
     * @return a [PointTripWithData]
     */
    private fun getPointFromButtonId(idButton: Int): PointTripWithData{
        return when(idButton) {
            R.id.add_trip_start_point_user_location,
            R.id.add_trip_start_point_pick,
            R.id.add_trip_start_point_time_text -> startPointLD.value!!
            R.id.add_trip_end_point_user_location,
            R.id.add_trip_end_point_pick,
            R.id.add_trip_end_point_time_text -> endPointLD.value!!
            else -> throw IllegalArgumentException("Unknown button $idButton")
        }
    }


    /**
     * Save the Trip to the database
     *
     * @see checkErrors
     * @see TripRepository.createTrip
     *
     */
    fun startTrip(context: Context){

        fun createTripInDatabase(){
            with(trip.trip){
                if (mainLocation.isNullOrBlank()){
                    val startPoint = _startPointLD.value!!
                    mainLocation = startPoint.location?.city ?: startPoint.location?.country
                }
            }
            _tripLD.emitNewValue()

            viewModelScope.launch {
                when(tripRepository.createTrip(trip, checkList)){
                    is Result.Success -> {
                        configureCheckUpWorkManager(context)
                        _tripSavedLD.value = Event(Unit)
                    }
                    is Result.Error -> showSnackBarMessage(R.string.trip_creation_error)
                    is Result.Canceled -> showSnackBarMessage(R.string.trip_creation_error)

                }
                _dataLoading.value = false
            }
        }

        /**
         * Fetch location and weather information for every points
         *
         * @see TripRepository.fetchPointLocationInformation
         *
         */
        fun fetchPointLocationInformation(){
            viewModelScope.launch {
                when(tripRepository.fetchPointLocationInformation(*trip.points.toTypedArray())){
                    is Result.Error -> showSnackBarMessage(R.string.trip_creation_error)
                    is Result.Canceled -> showSnackBarMessage(R.string.trip_creation_error)

                    is Result.Success -> createTripInDatabase()

                }

            }
        }
        _dataLoading.value = true
        if (!checkErrors()){
            fetchPointLocationInformation()
        } else {
            _dataLoading.value = false
        }
    }

    private fun configureCheckUpWorkManager(context: Context){
        if (trip.trip.updateFrequency != TripUpdateFrequency.NEVER){
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED).build()
            val frequencyUpdate = trip.trip.updateFrequency?.frequencyMillisecond ?: throw Exception("No frequency setup for $trip")
            val checkUpWorker = PeriodicWorkRequestBuilder<CheckUpWorker>(frequencyUpdate, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .setInputData(Data.Builder().putString(USER_ID_DATA, trip.trip.userId).build())
                .setInitialDelay(frequencyUpdate, TimeUnit.MILLISECONDS)
                .addTag(CHECK_UP_WORKER_TAG)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(CHECK_UP_WORKER_TAG, ExistingPeriodicWorkPolicy.REPLACE, checkUpWorker)
        }
    }

    /**
     * Check if the data entered by the user have any errors
     *
     * @return true if there is any errors otherwise false
     */
    private fun checkErrors(): Boolean {
        resetAllErrors()
        var error = false
        with(trip) {
            with(trip) {
                if (type == null) {
                    _typeError.value = R.string.cant_be_empty
                    error = true
                }
                if (updateFrequency == null) {
                    _updateFrequencyError.value = R.string.cant_be_empty
                    error = true
                }
            }
            with(watchers) {
                if (isEmpty()) {
                    _watcherError.value = R.string.cant_be_empty
                    error = true
                }
            }

            // remove all the stage points with empty latitude or lon
            val pointsToRemove = mutableListOf<PointTripWithData>()
            with(points.filterScheduleStage()) {
                forEach {
                    if (it.location?.latitude == null || it.location.longitude == null) {
                        pointsToRemove.add(it)
                    }
                }
            }
            points.removeAll(pointsToRemove)
            _stagePointsLD.emitNewValue()


        }
        with(_startPointLD.value!!) {
            if (location?.latitude == null) {
                _startPointLatError.value = R.string.cant_be_empty
                error = true
            }
            if (location?.longitude == null) {
                _startPointLngError.value = R.string.cant_be_empty
                error = true
            }
            if (pointTrip.time == null) {
                _startPointTimeError.value = R.string.cant_be_empty
                error = true
            }
        }

        with(_endPointLD.value!!) {
            if (location?.latitude == null) {
                _endPointLatError.value = R.string.cant_be_empty
                error = true
            }
            if (location?.longitude == null) {
                _endPointLngError.value = R.string.cant_be_empty
                error = true
            }
            if (pointTrip.time == null) {
                _endPointTimeError.value = R.string.cant_be_empty
                error = true
            }
        }
        return error
    }

    /**
     * Reset all the errors Live Data to null
     *
     */
    private fun resetAllErrors(){
        _startPointLngError.value = null
        _startPointLatError.value = null
        _endPointLatError.value = null
        _endPointLngError.value = null
        _typeError.value = null
        _watcherError.value = null
        _updateFrequencyError.value = null
        _startPointTimeError.value = null
        _endPointTimeError.value = null
    }


}