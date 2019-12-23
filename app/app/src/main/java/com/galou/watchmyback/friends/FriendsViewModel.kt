package com.galou.watchmyback.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.galou.watchmyback.Event
import com.galou.watchmyback.base.UserListBaseViewModel
import com.galou.watchmyback.data.repository.FriendRepository
import com.galou.watchmyback.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * [ViewModel] for [FriendsView]
 * Inherit from [UserListBaseViewModel]
 *
 * @property userRepository [UserRepository] reference
 * @property friendRepository [FriendRepository] reference
 */
class FriendsViewModel(
    private val userRepository: UserRepository,
    private val friendRepository: FriendRepository
) : UserListBaseViewModel(userRepository, friendRepository) {


    private val _openAddFriendLD = MutableLiveData<Event<Unit>>()
    val openAddFriendLD: LiveData<Event<Unit>> = _openAddFriendLD

    /**
     * Refresh the list of friend
     *
     * @see fetchFriends
     *
     */
    fun refreshFriendList(){
        fetchFriends(true)
    }

    /**
     * Show the Add Friend Activty
     *
     */
    fun openAddFriendsActivity(){
        _openAddFriendLD.value = Event(Unit)
    }

    /**
     * Fetch all the friends of the current user
     *
     * @param refresh determine if the list should be refreshed or not
     *
     * @see fetchResultUsers
     * @see FriendRepository.fetchUserFriend
     */
    fun fetchFriends(refresh: Boolean = false){
        viewModelScope.launch { fetchResultUsers(friendRepository.fetchUserFriend(userLD.value!!, refresh)) }
    }


}