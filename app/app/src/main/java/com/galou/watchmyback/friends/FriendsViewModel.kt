package com.galou.watchmyback.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galou.watchmyback.R
import com.galou.watchmyback.base.BaseViewModel
import com.galou.watchmyback.data.entity.OtherUser
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.repository.FriendRepository
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.displayData
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * @author galou
 * 2019-11-05
 */
class FriendsViewModel(
    private val userRepository: UserRepository,
    private val friendRepository: FriendRepository
) : BaseViewModel(){

    private val _friendsLD = MutableLiveData<List<OtherUser>>()
    val friendsLD: LiveData<List<OtherUser>> = _friendsLD

    private var user: User = userRepository.currentUser.value!!


    init {
        fetchFriends()

    }

    fun removeFriend(friend: OtherUser){
        _dataLoading.value = true
        viewModelScope.launch {
            when(friendRepository.removeFriend(user, friend.id)){
                is Result.Success -> fetchFriends()
                is Result.Error -> showSnackBarMessage(R.string.error_occured)
                is Result.Canceled -> showSnackBarMessage(R.string.canceled)
            }
        }
    }

    fun refreshFriendList(){
        fetchFriends(true)
    }

    private fun fetchFriends(refresh: Boolean = false){
        _dataLoading.value = true
        viewModelScope.launch {
            when(val friends = friendRepository.fetchUserFriend(user, refresh)){
                is Result.Success -> showListFriends(friends.data)
                is Result.Error -> showSnackBarMessage(R.string.error_fetch_friends)
                is Result.Canceled -> showSnackBarMessage(R.string.canceled)
            }
        }
    }

    private fun showListFriends(friends: List<OtherUser>){
        if (friends.isNotEmpty()) _friendsLD.value = friends
        else showSnackBarMessage(R.string.no_friends_yet)
        _dataLoading.value = false
    }



}