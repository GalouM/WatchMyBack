package com.galou.watchmyback.addModifyCheckList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.galou.watchmyback.Event
import com.galou.watchmyback.R
import com.galou.watchmyback.addModifyCheckList.ActionCheckList.*
import com.galou.watchmyback.base.BaseViewModel
import com.galou.watchmyback.data.entity.CheckList
import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.data.entity.ItemCheckList
import com.galou.watchmyback.data.entity.TripType
import com.galou.watchmyback.data.repository.CheckListRepository
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.displayData
import com.galou.watchmyback.utils.idGenerated
import kotlinx.coroutines.launch

/**
 * @author galou
 * 2019-11-22
 */
class AddModifyCheckListViewModel(
    private val checkListRepository: CheckListRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val tripeType = TripType.values().toList()

    private val _typesCheckList = MutableLiveData<Event<List<TripType>>>()
    val typesCheckList: LiveData<Event<List<TripType>>> = _typesCheckList

    private val _checkListLD = MutableLiveData<CheckList>()
    val checkListLD: LiveData<CheckList> = _checkListLD

    private val _itemsCheckListLD = MutableLiveData<MutableList<ItemCheckList>>()
    val itemsCheckListLD: LiveData<MutableList<ItemCheckList>> = _itemsCheckListLD
    
    private val _checkListSavedLD = MutableLiveData<Event<Unit>>()
    val checkListSavedLD: LiveData<Event<Unit>> = _checkListSavedLD

    private val _checkListDeletedLD = MutableLiveData<Event<Unit>>()
    val checkListDeletedLD: LiveData<Event<Unit>> = _checkListDeletedLD
    
    private val _errorNameLD = MutableLiveData<Int?>()
    val errorNameLD: LiveData<Int?> = _errorNameLD
    
    private val _errorTypeLD = MutableLiveData<Int?>()
    val errorTypeLD: LiveData<Int?> = _errorTypeLD
    
    private val _modifyCheckList = MutableLiveData<Boolean>()
    val modifyCheckList: LiveData<Boolean> = _modifyCheckList

    private val actionType: ActionCheckList

    init {
        _dataLoading.value = true
        actionType = when(val selectedCheckList = checkListRepository.checkList){
            null -> {
                _modifyCheckList.value = false
                _checkListLD.value = CheckList(
                    id = idGenerated,
                    userId = userRepository.currentUser.value?.id ?: throw IllegalAccessError("User is null"))
                _itemsCheckListLD.value = mutableListOf()
                _dataLoading.value = false
                ADD
            }
            else -> {
                _modifyCheckList.value = true
                _checkListLD.value = selectedCheckList
                fetchItemsCheckList(selectedCheckList)
                MODIFY
            }
        }
    }
    
    fun saveCheckList(){
        _dataLoading.value = true
        resetErrors()
        val checkList = _checkListLD.value ?: throw IllegalAccessException("CheckList is null")
        val items = _itemsCheckListLD.value!!
        if (!checkErrors(checkList, items)) {
            when(actionType){
                ADD -> saveNewCheckList(checkList, items)
                MODIFY -> updateCheckList(checkList, items)
            }
        } else {
            _dataLoading.value = false
        }
    }
    
    fun addItem(){
        _itemsCheckListLD.value!!.add(ItemCheckList(
            id = idGenerated,
            listId = _checkListLD.value!!.id))
        _itemsCheckListLD.apply {
            value = this.value
        }
    }

    fun removeItem(item: ItemCheckList){
        _itemsCheckListLD.value!!.remove(item)
        _itemsCheckListLD.apply {
            value = this.value
        }
    }
    
    fun deleteCheckList(){
        _dataLoading.value = true
        viewModelScope.launch { 
            when(checkListRepository.deleteCheckList(
                CheckListWithItems(
                    checkList = _checkListLD.value!!,
                    items = _itemsCheckListLD.value!!
            ))){
                is Result.Success -> _checkListDeletedLD.value = Event(Unit)
                is Result.Canceled, is Result.Error -> showSnackBarMessage(R.string.error_delete_checklist)
            }
        }
        _dataLoading.value = false
    }

    fun showCheckListTypeDialog(){
        _typesCheckList.value = Event(tripeType)
    }

    fun selectCheckListType(type: TripType){
        _checkListLD.value!!.tripType = type
        _checkListLD.run { value = this.value }
    }

    private fun fetchItemsCheckList(checkList: CheckList){
        viewModelScope.launch { 
            when(val result = checkListRepository.fetchCheckList(checkList, false)){
                is Result.Success -> _itemsCheckListLD.value = result.data?.items?.toMutableList() ?: throw IllegalAccessException("Items is null")
                is Result.Canceled, is Result.Error -> showSnackBarMessage(R.string.error_fetch_checklist)
            }
        }
        _dataLoading.value = false
    }
    
    private fun saveNewCheckList(checkList: CheckList, items: List<ItemCheckList>){
        viewModelScope.launch { 
            showResultSavedCheckList(checkListRepository.createCheckList(
                checkList = checkList,
                items = items
            ))
        }
    }
    
    private fun updateCheckList(checkList: CheckList, items: List<ItemCheckList>){
        viewModelScope.launch { 
            showResultSavedCheckList(checkListRepository.updateCheckList(
                checkList = checkList,
                items = items
            ))
        }
    }
    
    private fun showResultSavedCheckList(result: Result<Void?>){
        when(result){
            is Result.Success -> _checkListSavedLD.value = Event(Unit)
            is Result.Canceled, is Result.Error -> showSnackBarMessage(R.string.error_saving_list)
        }
        _dataLoading.value = false
    }
    
    private fun checkErrors(checkList: CheckList, items: List<ItemCheckList>): Boolean {
        var error = false
        if (checkList.name.isBlank()) {
            _errorNameLD.value = R.string.eror_name_checklist
            error = true
        }
        if (checkList.tripType == null) {
            _errorTypeLD.value = R.string.error_type_checklist
            error = true
        }
        if (items.isEmpty()) {
            showSnackBarMessage(R.string.error_item_checklist)
            error = true
        }

        items.forEach { if (it.name.isBlank()) removeItem(it) }
        return error
    }

    private fun resetErrors(){
        _errorTypeLD.value = null
        _errorNameLD.value = null
    }


}

enum class ActionCheckList(){
    ADD,
    MODIFY
}