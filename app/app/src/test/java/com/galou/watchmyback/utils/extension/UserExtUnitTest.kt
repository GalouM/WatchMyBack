package com.galou.watchmyback.utils.extension

import com.galou.watchmyback.testHelpers.firstFriend
import com.galou.watchmyback.testHelpers.generateTestUser
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
        assertThat(otherUser.user.id).isEqualTo(firstFriend.id)
        assertThat(otherUser.user.username).isEqualTo(firstFriend.username)
        assertThat(otherUser.user.email).isEqualTo(firstFriend.email)
        assertThat(otherUser.user.phoneNumber).isEqualTo(firstFriend.phoneNumber)
        assertThat(otherUser.user.pictureUrl).isEqualTo(firstFriend.pictureUrl)
    }

    @Test
    @Throws(Exception::class)
    fun convertListUserToListOtherUser_keepProperty(){
        val otherUsers = listOf(firstFriend, secondFriend).toListOtherUser(true)
        assertThat(otherUsers[0].user.id).isEqualTo(firstFriend.id)
        assertThat(otherUsers[0].user.username).isEqualTo(firstFriend.username)
        assertThat(otherUsers[0].user.email).isEqualTo(firstFriend.email)
        assertThat(otherUsers[0].user.phoneNumber).isEqualTo(firstFriend.phoneNumber)
        assertThat(otherUsers[0].user.pictureUrl).isEqualTo(firstFriend.pictureUrl)
        assertThat(otherUsers[1].user.id).isEqualTo(secondFriend.id)
        assertThat(otherUsers[1].user.username).isEqualTo(secondFriend.username)
        assertThat(otherUsers[1].user.email).isEqualTo(secondFriend.email)
        assertThat(otherUsers[1].user.phoneNumber).isEqualTo(secondFriend.phoneNumber)
        assertThat(otherUsers[1].user.pictureUrl).isEqualTo(secondFriend.pictureUrl)
    }

    @Test
    @Throws(Exception::class)
    fun dropCurrentUserFromListUser_dropCurrentUser(){
        val mainUser = generateTestUser("Test_id").toOtherUser(false)
        val listUser = listOf(firstFriend.toOtherUser(true), mainUser, secondFriend.toOtherUser(true))
        val listWithoutCurrentUser = listUser.removeCurrentUser(mainUser.user.id)
        assertThat(listWithoutCurrentUser).doesNotContain(mainUser)
    }




}