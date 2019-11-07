package com.galou.watchmyback.data.entity

/**
 * @author galou
 * 2019-11-07
 */
data class OtherUser(
    val id: String,
    val username: String,
    val emailAddress: String,
    val phoneNumber: String?,
    val pictureUrl: String?,
    var isMyFriend: Boolean

)