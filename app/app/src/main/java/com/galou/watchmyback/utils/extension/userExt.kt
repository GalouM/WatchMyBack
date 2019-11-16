package com.galou.watchmyback.utils.extension

import com.galou.watchmyback.data.entity.OtherUser
import com.galou.watchmyback.data.entity.User

/**
 * @author galou
 * 2019-11-07
 */

fun User.toOtherUser(myFriend: Boolean): OtherUser {
    return OtherUser(
        user = this,
        myFriend = myFriend
        )
}

fun List<User>.toListOtherUser(myFriend: Boolean): List<OtherUser> {
    val otherUsers = mutableListOf<OtherUser>()
    this.forEach { user ->
        otherUsers.add(user.toOtherUser(myFriend)) }
    return otherUsers
}


infix fun List<OtherUser>.removeCurrentUser(currentUserId: String) =
    when (val currentUser = firstOrNull { it.user.id == currentUserId }) {
        null -> this
        else -> {
            val mutable = this.toMutableList()
            mutable.remove(currentUser)
            mutable
        }
    }