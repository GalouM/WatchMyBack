package com.galou.watchmyback.checklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.galou.watchmyback.Event
import com.galou.watchmyback.R
import com.galou.watchmyback.base.BaseViewModel
import com.galou.watchmyback.data.entity.CheckList
import com.galou.watchmyback.data.repository.CheckListRepository
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.utils.Result
import kotlinx.coroutines.launch

/**
 * [ViewModel] for [CheckListView]
 *
 * Inherit from [BaseViewModel]
 *
 * @property userRepository [UserRepository] reference
 * @property checkListRepository [CheckListRepository] reference
 */
class CheckListViewModel(
    private val userRepository: UserRepository,
    private val checkListRepository: CheckListRepository
    ) : BaseViewModel() {
    
    private val _checkListLD = MutableLiveData<List<CheckList>>()
    val checkListLD: LiveData<List<CheckList>> = _checkListLD
    
    private val _openAddModifyCheckList = MutableLiveData<Event<Unit>>()
    val openAddModifyCheckList: LiveData<Event<Unit>> = _openAddModifyCheckList
    
    private val currentUser = userRepository.currentUser.value!!

    /**
     *
     *
     * @see CheckListRespository.checkListFetched
     * @see fetchCheckLists
     */
    init {
        _dataLoading.value = true
        fetchCheckLists(!checkListRepository.checkListFetched)
        checkListRepository.checkListFetched = true
    }

    /**
     * Refresh the check list shown and make the repository fetch them from the remote database
     *
     * @see fetchCheckLists
     *
     */
    fun refresh(){
        _dataLoading.value = true
        fetchCheckLists(true)
    }


    /**
     * Open the activity to modify a check list
     * Set the check list selected with the user's selection
     *
     * @param checkList check list selected by the user
     */
    fun modifyCheckList(checkList: CheckList){
        checkListRepository.checkList = checkList
        _openAddModifyCheckList.value = Event(Unit)

    }

    /**
     * Open the activity to create a new checklist
     * Set the check list selected as null
     *
     * @see CheckListRepository.checkList
     *
     */
    fun addCheckList(){
        checkListRepository.checkList = null
        _openAddModifyCheckList.value = Event(Unit)
    }

    /**
     * Fetch all the check list of the current user
     *
     * @param refresh determine if the list should be fetched from the local or remote database
     *
     * @see CheckListRepository.fetchUserCheckLists
     */
    private fun fetchCheckLists(refresh: Boolean = false){
        viewModelScope.launch {
            when(val checkListsResult = checkListRepository.fetchUserCheckLists(currentUser.id, refresh)){
                is Result.Success ->{
                    val checkLists = checkListsResult.data.map { it.checkList }
                    if (checkLists.isEmpty()) showSnackBarMessage(R.string.no_checkList)
                    else _checkListLD.value = checkLists
                }
                is Result.Canceled, is Result.Error -> showSnackBarMessage(R.string.error_fetch_check_lists)
            }
            _dataLoading.value = false
        }

    }

}