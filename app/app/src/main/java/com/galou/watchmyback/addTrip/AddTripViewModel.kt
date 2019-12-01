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
import com.galou.watchmyback.utils.extension.idButtonToPointTrip
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
    val itemCheckListLD: LiveData<List<ItemCheckList>> = _itemsCheckListLD

    private val _openAddCheckListLD = MutableLiveData<Event<Unit>>()
    val openAddCheckListLD: LiveData<Event<Unit>> = _openAddCheckListLD

    private val _openAddFriendLD = MutableLiveData<Event<Unit>>()
    val openAddFriendLD: LiveData<Event<Unit>> = _openAddFriendLD

    private val _startPointLD = MutableLiveData<PointTripWithData>()
    val startPointLD: LiveData<PointTripWithData> = _startPointLD

    private val _endPointLD = MutableLiveData<PointTripWithData>()
    val endPointLD: LiveData<PointTripWithData> = _endPointLD

    private val _stagePointsLD = MutableLiveData<MutableList<PointTripWithData>>()
    val stagePointsLD: LiveData<MutableList<PointTripWithData>> = _stagePointsLD


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
        _watchersLD.value = watchers
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

    fun selectCheckList(checkList: CheckListWithItems){
        trip.trip.checkListId = checkList.checkList.id
        _itemsCheckListLD.value = checkList.items
        updateTripLDValue()
    }

    fun selectUpdateFrequency(frequency: TripUpdateFrequency){
        _tripLD.value!!.updateFrequency = frequency
        updateTripLDValue()
    }

    fun selectTripType(type: TripType){
        trip.trip.type = type
        updateTripLDValue()
    }

    fun clickAddCheckList(){
        checkListRepository.checkList = null
        _openAddCheckListLD.value = Event(Unit)
    }

    fun clickAddFriends(){
        _openAddFriendLD.value = Event(Unit)
    }

    fun addStagePoint(){
        if (stagePointsLD.value == null)_stagePointsLD.value = mutableListOf()
        else {
            _stagePointsLD.value?.add(PointTripWithData(
                pointTrip = PointTrip(
                    typePoint = TypePoint.SCHEDULE_STAGE,
                    tripId = trip.trip.id
                )
            ))
            _stagePointsLD.run { value = this.value }
        }
    }

    fun setPointFromCurrentLocation(idButton: Int) {
        fetchCurrentLocation(idButton.idButtonToPointTrip(trip.trip.id))
    }

    fun setPointFromMap(idButton: Int) {
        displayMapWithMarker(idButton.idButtonToPointTrip(trip.trip.id))
    }

    fun setStagePointFromCurrentLocation(point: PointTripWithData){
        fetchCurrentLocation(point)
    }

    fun setStagePointFromMap(point: PointTripWithData){
        displayMapWithMarker(point)
    }

    private fun fetchCurrentLocation(point: PointTripWithData){
        TODO()
    }

    private fun displayMapWithMarker(point: PointTripWithData){
        TODO()
    }

    private fun updateTripLDValue(){
        _tripLD.run { value = this.value }
    }


}