package com.galou.watchmyback.utils.extension

import com.galou.watchmyback.data.entity.OtherUser
import com.galou.watchmyback.data.entity.User

/**
 * @author galou
 * 2019-11-07
 */

/**
 * Convert a [User] into a [OtherUser] object
 *
 * Allow to determine if the user is friend with the current user or not
 *
 * @param myFriend
 * @return a [OtherUser] object
 *
 */
fun User.toOtherUser(myFriend: Boolean): OtherUser {
    return OtherUser(
        user = this,
        myFriend = myFriend
        )
}

/**
 * Convert a List of [User] into a list of [OtherUser]
 * Allow to determine if each user is friend with the current user or not
 *
 * @param myFriend
 * @return List of [OtherUser]
 *
 * @see User.toOtherUser
 */
fun List<User>.toListOtherUser(myFriend: Boolean): List<OtherUser> {
    val otherUsers = mutableListOf<OtherUser>()
    this.forEach { user ->
        otherUsers.add(user.toOtherUser(myFriend)) }
    return otherUsers
}


/**
 * Remove the current user from a list of user
 *
 * @param currentUserId ID of the current user
 */
infix fun List<OtherUser>.removeCurrentUser(currentUserId: String) =
    this.filter { it.user.id != currentUserId }

/**
 * Detect the user's friend and set up the my friend parameter in a list of users
 *
 * @param myFriends list of the current user friends' id
 */
infix fun List<OtherUser>.setIsMyFriend(myFriends: List<String>) =
    forEach { it.myFriend = myFriends.contains(it.user.id) }
