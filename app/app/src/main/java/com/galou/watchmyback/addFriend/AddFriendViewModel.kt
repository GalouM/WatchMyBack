package com.galou.watchmyback.addFriend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.galou.watchmyback.addFriend.FetchType.*
import com.galou.watchmyback.base.UserListBaseViewModel
import com.galou.watchmyback.data.repository.FriendRepository
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.utils.displayData
import kotlinx.coroutines.launch

/**
 * @author galou
 * 2019-11-07
 */
class AddFriendViewModel(
    private val userRepository: UserRepository,
    private val friendRepository: FriendRepository
) : UserListBaseViewModel(userRepository, friendRepository) {


    private val _fetchType = MutableLiveData<FetchType>(USERNAME)
    val fetchType: LiveData<FetchType> = _fetchType

    val searchPattern = MutableLiveData<String>("")

    init {
        fetchUsers()
    }

    fun fetchUsers(){
        when {
            searchPattern.value!!.isBlank() -> fetchAllUsers()
            _fetchType.value == USERNAME -> fetchUserByUsername()
            _fetchType.value == EMAIL_ADDRESS -> fetUserByEmailAddress()
            else -> fetchAllUsers()
        }

    }

    fun changeTypeSearch(fetchType: FetchType){
        _fetchType.value = fetchType
        fetchUsers()
    }

    private fun fetchAllUsers(){
        _dataLoading.value = true
        viewModelScope.launch { fetchResultUsers(userRepository.fetchAllUsers()) }
    }

    private fun fetchUserByUsername() {
        _dataLoading.value = true
        _fetchType.value = USERNAME
        viewModelScope.launch { fetchResultUsers(userRepository.fetchUserByUsername(searchPattern.value!!)) }
    }

    private fun fetUserByEmailAddress() {
        _dataLoading.value = true
        _fetchType.value = EMAIL_ADDRESS
        viewModelScope.launch { fetchResultUsers(userRepository.fetchUserByEmailAddress(searchPattern.value!!)) }
    }


}

enum class FetchType {
    USERNAME,
    EMAIL_ADDRESS
}