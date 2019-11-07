package com.galou.watchmyback.utils.extension

import com.galou.watchmyback.testHelpers.firstFriend
import com.galou.watchmyback.testHelpers.secondFriend
import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * @author galou
 * 2019-11-07
 */
class UserExtUnitTest {

    @Test
    @Throws(Exception::class)
    fun convertUserToOtherUser_keepProperty(){
        val otherUser = firstFriend.toOtherUser(true)
        assertThat(otherUser.id).isEqualTo(firstFriend.id)
        assertThat(otherUser.username).isEqualTo(firstFriend.username)
        assertThat(otherUser.emailAddress).isEqualTo(firstFriend.email)
        assertThat(otherUser.phoneNumber).isEqualTo(firstFriend.phoneNumber)
        assertThat(otherUser.pictureUrl).isEqualTo(firstFriend.pictureUrl)
    }

    @Test
    @Throws(Exception::class)
    fun convertListUserToListOtherUser_keepProperty(){
        val otherUsers = listOf(firstFriend, secondFriend).toListOtherUser(true)
        assertThat(otherUsers[0].id).isEqualTo(firstFriend.id)
        assertThat(otherUsers[0].username).isEqualTo(firstFriend.username)
        assertThat(otherUsers[0].emailAddress).isEqualTo(firstFriend.email)
        assertThat(otherUsers[0].phoneNumber).isEqualTo(firstFriend.phoneNumber)
        assertThat(otherUsers[0].pictureUrl).isEqualTo(firstFriend.pictureUrl)
        assertThat(otherUsers[1].id).isEqualTo(secondFriend.id)
        assertThat(otherUsers[1].username).isEqualTo(secondFriend.username)
        assertThat(otherUsers[1].emailAddress).isEqualTo(secondFriend.email)
        assertThat(otherUsers[1].phoneNumber).isEqualTo(secondFriend.phoneNumber)
        assertThat(otherUsers[1].pictureUrl).isEqualTo(secondFriend.pictureUrl)
    }


}