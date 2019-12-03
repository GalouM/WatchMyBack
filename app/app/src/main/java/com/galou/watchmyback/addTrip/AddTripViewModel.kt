package com.galou.watchmyback.addTrip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.galou.watchmyback.Event
import com.galou.watchmyback.R
import com.galou.watchmyback.base.BaseViewModel
import com.galou.watchmyback.data.entity.*
import com.galou.watchmyback.data.repository.CheckListRepository
import com.galou.watchmyback.data.repository.FriendRepository
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.extension.emitNewValue
import com.galou.watchmyback.utils.extension.filterOrCreateMainPoint
import com.galou.watchmyback.utils.extension.filterScheduleStage
import com.galou.watchmyback.utils.extension.toWatchers
import kotlinx.coroutines.launch

/**
 * @author galou
 * 2019-11-30
 */

class AddTripViewModel(
    private val friendRepository: FriendRepository,
    val userRepository: UserRepository,
    private val checkListRepository: CheckListRepository
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

    private val _friendsLD = MutableLiveData<Event<List<User>>>()
    val friendsLD: LiveData <Event<List<User>>> = _friendsLD

    private val _openPickCheckListLD = MutableLiveData<Event<List<CheckListWithItems>>>()
    val openPickCheckListLD: LiveData<Event<List<CheckListWithItems>>> = _openPickCheckListLD

    private val _itemsCheckListLD = MutableLiveData<List<ItemCheckList>>()
        get() = field.apply { value = checkList?.items }
    val itemCheckListLD: LiveData<List<ItemCheckList>> = _itemsCheckListLD

    private val _openAddCheckListLD = MutableLiveData<Event<Unit>>()
    val openAddCheckListLD: LiveData<Event<Unit>> = _openAddCheckListLD

    private val _openAddFriendLD = MutableLiveData<Event<Unit>>()
    val openAddFriendLD: LiveData<Event<Unit>> = _openAddFriendLD

    private val _startPointLD = MutableLiveData<PointTripWithData>()
        get() {
            if (field.value == null)  field.apply {
                value = trip.points.filterOrCreateMainPoint(TypePoint.START, trip.trip.id)
            }
            return field
        }
    val startPointLD: LiveData<PointTripWithData> = _startPointLD

    private val _endPointLD = MutableLiveData<PointTripWithData>()
        get() = field.apply { value = trip.points.filterOrCreateMainPoint(TypePoint.END, trip.trip.id) }
    val endPointLD: LiveData<PointTripWithData> = _endPointLD

    private val _stagePointsLD = MutableLiveData<MutableList<PointTripWithData>>()
        get() = field.apply { value = trip.points.filterScheduleStage() }
    val stagePointsLD: LiveData<MutableList<PointTripWithData>> = _stagePointsLD

    private val _openMapLD = MutableLiveData<Event<PointTripWithData>>()
    val openMapLD: LiveData<Event<PointTripWithData>> = _openMapLD


    fun showTripTypeDialog(){
        _typesTrip.value = Event(tripeType)
    }

    fun showTripUpdateFrequency(){
        _updateHzTripLD.value = Event(tripUpdateFrequency)
    }

    fun showFriendsList(){
        _dataLoading.value = true
        viewModelScope.launch {
            when(val result = friendRepository.fetchUserFriend(
                currentUser,
                false)){
                is Result.Success -> {
                    _friendsLD.value = Event(result.data.map { it.user })
                }
                is Result.Error, is Result.Canceled -> showSnackBarMessage(R.string.error_fetch_friends)
            }
            _dataLoading.value = false
        }
    }

    fun selectWatchers(watchers: List<User>){
        trip.watchers = watchers
        _watchersLD.emitNewValue()
    }

    fun clickPickCheckList(){
        val tripType = tripLD.value?.type
        if (tripType == null){
            showSnackBarMessage(R.string.select_type_first)
        } else {
            _dataLoading.value = true
            viewModelScope.launch {
                val refresh = !checkListRepository.checkListFetched
                when(val result = checkListRepository.fetchUserCheckLists(currentUser.id, refresh)){
                    is Result.Success -> {
                        checkListRepository.checkListFetched = true
                        _openPickCheckListLD.value = Event(result.data.filter { it.checkList.tripType == tripType })
                    }
                    is Result.Error, is Result.Canceled -> showSnackBarMessage(R.string.error_fetch_check_lists)
                }
                _dataLoading.value = false
            }
        }
        
    }

    fun selectCheckList(checkListPicked: CheckListWithItems){
        checkList = checkListPicked
        trip.trip.checkListId = checkListPicked.checkList.id
        checkList?.items?.forEach { it.checked = false }

        _itemsCheckListLD.emitNewValue()
        _tripLD.emitNewValue()
    }

    fun clickOnItemCheckListBox(items: ItemCheckList){
        items.apply { checked = !checked }
    }

    fun selectUpdateFrequency(frequency: TripUpdateFrequency){
        trip.trip.updateFrequency = frequency
        _tripLD.emitNewValue()
    }

    fun selectTripType(type: TripType){
        trip.trip.type = type
        _tripLD.emitNewValue()
    }

    fun clickAddCheckList(){
        checkListRepository.checkList = null
        _openAddCheckListLD.value = Event(Unit)
    }

    fun clickAddFriends(){
        _openAddFriendLD.value = Event(Unit)
    }

    fun addStagePoint(){
        trip.points.add(PointTripWithData(
            pointTrip = PointTrip(
                typePoint = TypePoint.SCHEDULE_STAGE,
                tripId = trip.trip.id
            )
        ))
        _stagePointsLD.emitNewValue()
    }

    fun setPointFromCurrentLocation(idButton: Int) {
        fetchCurrentLocation(getPointFromButtonId(idButton))
    }

    fun setPointFromMap(idButton: Int) {
        displayMapWithMarker(getPointFromButtonId(idButton))
    }

    fun setStagePointFromCurrentLocation(point: PointTripWithData){
        fetchCurrentLocation(point)
    }

    fun setStagePointFromMap(point: PointTripWithData){
        displayMapWithMarker(point)
    }

    fun startTrip(){
        _dataLoading.value = true
        val tripWatcher = trip.watchers toWatchers trip.trip.id
        fetchLocationInformation()
        fetchWeatherDataForPoints()
        with(trip.trip){
            if (mainLocation.isNullOrBlank()){
                val startPoint = trip.points.filterOrCreateMainPoint(TypePoint.START, trip.trip.id)
                mainLocation = startPoint.location?.city
            }
        }

    }

    fun setPointLocation(lat: Double, lgn: Double, point: PointTripWithData){
        point.location!!.apply {
            latitude = lat
            longitude = lgn
        }
        _tripLD.emitNewValue()
    }

    private fun fetchCurrentLocation(point: PointTripWithData){
        TODO()
    }

    private fun displayMapWithMarker(point: PointTripWithData){
        _openMapLD.value = Event(point)
    }

    private fun getPointFromButtonId(idButton: Int): PointTripWithData{
        return when(idButton) {
            R.id.add_trip_start_point_user_location, R.id.add_trip_start_point_pick -> startPointLD.value!!
            R.id.add_trip_end_point_user_location, R.id.add_trip_end_point_pick -> endPointLD.value!!
            else -> throw IllegalArgumentException("Unknown button")
        }
    }

    private fun fetchWeatherDataForPoints(){
        TODO()
    }
    
    private fun fetchLocationInformation(){
        TODO()
    }


}