package com.galou.watchmyback.utils.extension

import com.galou.watchmyback.data.entity.User
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

    @Test
    @Throws(Exception::class)
    fun setupIsMyFriendCorrectly(){
        val mainUser = generateTestUser("Test_id")
        mainUser.friendsId.clear()
        mainUser.friendsId.add(firstFriend.id)
        val listAllUsers = listOf(firstFriend.toOtherUser(false), secondFriend.toOtherUser(false))
        listAllUsers setIsMyFriend mainUser.friendsId
        assertThat(listAllUsers[0].myFriend).isTrue()
        assertThat(listAllUsers[1].myFriend).isFalse()
    }

    @Test
    @Throws(Exception::class)
    fun convertListUserInWatcher_createCorrectWatcher(){
        val tripId = "tripId"
        val listUser = listOf(firstFriend, secondFriend)
        val watchers = listUser.toTripWatchers(tripId)
        assertThat(watchers).hasSize(listUser.size)
        val watcher1 = watchers[0]
        assertThat(watcher1.tripId).isEqualTo(tripId)
        assertThat(watcher1.watcherId).isEqualTo(firstFriend.id)
    }

    @Test
    @Throws(Exception::class)
    fun convertUserInWatcher_setProperly(){
        val listWatchers = listOf(firstFriend, secondFriend)
        val listFriends = listOf(firstFriend, User(), secondFriend, User())
        val friendsToWatchers = listFriends toWatcher listWatchers
        assertThat(friendsToWatchers[0].watchTrip).isTrue()
        assertThat(friendsToWatchers[1].watchTrip).isFalse()
        assertThat(friendsToWatchers[2].watchTrip).isTrue()
        assertThat(friendsToWatchers[3].watchTrip).isFalse()
    }




}