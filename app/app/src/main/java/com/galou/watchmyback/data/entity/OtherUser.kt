package com.galou.watchmyback.data.entity

/**
 * Represent a user who is not the current user
 *
 * @property user user's information
 * @property myFriend if the user if friend with the current user or not
 */
data class OtherUser(
    val user: User,
    var myFriend: Boolean

)