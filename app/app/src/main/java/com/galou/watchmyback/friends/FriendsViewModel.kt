package com.galou.watchmyback.friends

import androidx.lifecycle.ViewModel
import com.galou.watchmyback.data.repository.FriendRepository
import com.galou.watchmyback.data.repository.UserRepository

/**
 * @author galou
 * 2019-11-05
 */
class FriendsViewModel(
    private val userRepository: UserRepository,
    private val friendRepository: FriendRepository
) : ViewModel(){

}