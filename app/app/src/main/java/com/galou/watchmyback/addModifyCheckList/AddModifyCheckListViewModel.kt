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
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.idGenerated
import kotlinx.coroutines.launch

/**
 * @author galou
 * 2019-11-22
 */
class AddModifyCheckListViewModel(
    private val checkListRepository: CheckListRepository
) : BaseViewModel() {

    private val _typesCheckList = MutableLiveData<List<Int>>()
        get() {
            field.value = TripType.values().map { it.typeNameId }.toList()
            return field
        }
    val typesCheckList: LiveData<List<Int>> = _typesCheckList

    private val _checkListLD = MutableLiveData<CheckListWithItems>()
    val checkListLD: LiveData<CheckListWithItems> = _checkListLD
    
    private val _checkListSavedLD = MutableLiveData<Event<Unit>>()
    val checkListSavedLD: LiveData<Event<Unit>> = _checkListSavedLD

    private val _checkListDeletedLD = MutableLiveData<Event<Unit>>()
    val checkListDeletedLD: LiveData<Event<Unit>> = _checkListDeletedLD
    
    private val _errorNameLD = MutableLiveData<Int?>()
    val errorNameLD: LiveData<Int?> = _errorNameLD
    
    private val _errorTypeLD = MutableLiveData<Int?>()
    val errorTypeLD: LiveData<Int?> = _errorTypeLD
    
    private val _errorNoItems = MutableLiveData<Int?>()
    val errorNoItems: LiveData<Int?> = _errorNoItems
    
    private val _modifyCheckList = MutableLiveData<Boolean>()
    val modifyCheckList: LiveData<Boolean> = _modifyCheckList

    private val actionType: ActionCheckList

    init {
        _dataLoading.value = true
        actionType = when(val selectedCheckList = checkListRepository.checkList){
            null -> {
                _modifyCheckList.value = false
                _checkListLD.value = CheckListWithItems(
                    checkList = CheckList(idGenerated),
                    items = mutableListOf()
                )
                ADD
            }
            else -> {
                _modifyCheckList.value = true
                fetchItemsCheckList(selectedCheckList)
                MODIFY
            }
        }
    }
    
    fun saveCheckList(){
        _dataLoading.value = true
        val checkList = _checkListLD.value!!
        if (!checkErrors(checkList)) {
            when(actionType){
                ADD -> saveNewCheckList(checkList)
                MODIFY -> updateCheckList(checkList)
            }
        }
    }
    
    fun addItem(){
        _checkListLD.value!!.items.add(ItemCheckList(
            id = idGenerated,
            listId = checkListLD.value!!.checkList.id))
    }

    fun removeItem(item: ItemCheckList){
        _checkListLD.value!!.items.remove(item)
    }
    
    fun deleteCheckList(){
        _dataLoading.value = true
        viewModelScope.launch { 
            when(val result = checkListRepository.deleteCheckList(_checkListLD.value!!.checkList)){
                is Result.Success -> _checkListDeletedLD.value = Event(Unit)
                is Result.Canceled, is Result.Error -> showSnackBarMessage(R.string.error_delete_checklist)
            }
        }
        _dataLoading.value = false
    }

    private fun fetchItemsCheckList(checkList: CheckList){
        viewModelScope.launch { 
            when(val result = checkListRepository.fetchCheckList(checkList, false)){
                is Result.Success -> _checkListLD.value = result.data
                is Result.Error, is Result.Canceled -> showSnackBarMessage(R.string.error_fetch_checklist)
            }
        }
        _dataLoading.value = false
    }
    
    private fun saveNewCheckList(checkList: CheckListWithItems){
        viewModelScope.launch { 
            showResultSavedCheckList(checkListRepository.createCheckList(
                checkList = checkList.checkList,
                items = checkList.items
            ))
        }
    }
    
    private fun updateCheckList(checkList: CheckListWithItems){
        viewModelScope.launch { 
            showResultSavedCheckList(checkListRepository.updateCheckList(
                checkList = checkList.checkList,
                items = checkList.items
            ))
        }
    }
    
    private fun showResultSavedCheckList(result: Result<Void?>){
        when(result){
            is Result.Success -> _checkListSavedLD.value = Event(Unit)
            is Result.Error, is Result.Canceled -> showSnackBarMessage(R.string.error_saving_list)
        }
        _dataLoading.value = false
    }
    
    private fun checkErrors(checkList: CheckListWithItems): Boolean {
        var error = false
        if (checkList.checkList.name.isBlank()) {
            _errorNameLD.value = R.string.eror_name_checklist
            error = true
        }
        if (checkList.checkList.tripType == null) {
            _errorTypeLD.value = R.string.error_type_checklist
            error = true
        }
        if (checkList.items.isEmpty()) {
            _errorNoItems.value = R.string.error_item_checklist
            error = true
        }

        checkList.items.forEach { if (it.name.isBlank()) removeItem(it) }
        return error
    }


}

enum class ActionCheckList(){
    ADD,
    MODIFY
}