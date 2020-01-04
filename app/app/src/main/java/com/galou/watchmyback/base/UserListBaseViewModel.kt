package com.galou.watchmyback.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.galou.watchmyback.data.applicationUse.OtherUser
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.repository.FriendRepository
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.utils.extension.removeCurrentUser
import com.galou.watchmyback.utils.extension.setIsMyFriend
import kotlinx.coroutines.launch

/**
 * @author galou
 * 2019-11-08
 */
/**
 * [ViewModel] for views showing a list of user
 *
 * Inherit from [BaseViewModel]
 *
 * @property userRepository [UserRepositoryImp] reference
 * @property friendRepository [FriendRepositoryImpl] reference
 */
abstract class UserListBaseViewModel(
    private val userRepository: UserRepository,
    private val friendRepository: FriendRepository
) : BaseViewModel() {

    private val _usersLD = MutableLiveData<List<OtherUser>>()
    val usersLD: LiveData<List<OtherUser>> = _usersLD

    val userLD: LiveData<User> = userRepository.currentUser

    /**
     * Remove or add a friend to the current user
     *
     *
     * @param friend friend to add or remove
     *
     * @see FriendRepository.addFriend
     * @see FriendRepository.removeFriend
     */
    fun removeOrAddFriend(friend: OtherUser){
        _dataLoading.value = true
        viewModelScope.launch {
            when (friend.myFriend){
                true -> friendRepository.removeFriend(userLD.value!!, friend.user.id)
                false -> friendRepository.addFriend(userLD.value!!, friend.user)

            }
            showUsersList(usersLD.value!!)
        }

    }


    /**
     * Emit the list of user's the the view
     *
     * @param users lsit of users to display
     */
    fun showUsersList(users: List<OtherUser>){
        users setIsMyFriend userLD.value!!.friendsId
        _usersLD.value = users removeCurrentUser userLD.value!!.id
        _dataLoading.value = false
    }
}