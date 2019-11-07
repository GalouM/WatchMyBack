package com.galou.watchmyback.utils.extension

import com.galou.watchmyback.data.entity.OtherUser
import com.galou.watchmyback.data.entity.User

/**
 * @author galou
 * 2019-11-07
 */

fun User.toOtherUser(myFriend: Boolean): OtherUser {
    return OtherUser(
        id = this.id,
        username = this.username!!,
        emailAddress = this.email!!,
        phoneNumber = this.phoneNumber,
        pictureUrl = this.pictureUrl,
        isMyFriend = myFriend
        )
}

fun List<User>.toListOtherUser(myFriend: Boolean): List<OtherUser> {
    val otherUsers = mutableListOf<OtherUser>()
    this.forEach { user ->
        otherUsers.add(user.toOtherUser(myFriend)) }
    return otherUsers
}