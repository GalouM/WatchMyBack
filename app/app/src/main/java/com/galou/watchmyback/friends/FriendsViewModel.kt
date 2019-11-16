package com.galou.watchmyback.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.galou.watchmyback.Event
import com.galou.watchmyback.base.UserListBaseViewModel
import com.galou.watchmyback.data.repository.FriendRepository
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.utils.displayData
import kotlinx.coroutines.launch

/**
 * @author galou
 * 2019-11-05
 */
class FriendsViewModel(
    private val userRepository: UserRepository,
    private val friendRepository: FriendRepository
) : UserListBaseViewModel(userRepository, friendRepository) {


    private val _openAddFriendLD = MutableLiveData<Event<Unit>>()
    val openAddFriendLD: LiveData<Event<Unit>> = _openAddFriendLD

    init {
        fetchFriends(false)
    }

    fun refreshFriendList(){
        fetchFriends(true)
    }

    fun openAddFriendsActivity(){
        _openAddFriendLD.value = Event(Unit)
    }

    private fun fetchFriends(refresh: Boolean = false){
        viewModelScope.launch { fetchResultUsers(friendRepository.fetchUserFriend(currentUser, refresh)) }
    }


}