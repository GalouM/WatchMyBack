package com.galou.watchmyback.utils.extension

import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.remoteDBObject.TripWithDataRemoteDB
import com.galou.watchmyback.testHelpers.tripWithData
import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * @author galou
 * 2019-12-13
 */
class TripExtTest {

    @Test
    fun convertTripToTripDB_convertedCorrectly(){
        val trip = tripWithData
        val tripDB = trip.convertForRemoteDB()
        assertThat(tripDB.trip).isEqualTo(trip.trip)
        assertThat(tripDB.points).isEqualTo(trip.points)
        assertThat(tripDB.watchersId).containsExactlyElementsIn((trip.watchers.map { it.id }))
    }

    @Test
    fun convertTripDBToTrip_convertCorrectly(){
        val tripDB =
            TripWithDataRemoteDB()
        val listUser = mutableListOf<User>()
        tripDB.watchersId.forEach {
            listUser.add(User(id = it))
        }
        val trip = tripDB.convertForLocal(listUser)
        assertThat(trip.trip).isEqualTo(tripDB.trip)
        assertThat(trip.points).isEqualTo(tripDB.points)
        assertThat(trip.watchers.map { it.id }).containsExactlyElementsIn(tripDB.watchersId)
    }
}