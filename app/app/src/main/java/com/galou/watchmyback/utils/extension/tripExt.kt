package com.galou.watchmyback.utils.extension

import com.galou.watchmyback.data.entity.TripWithData
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.remoteDBObject.TripWithDataRemoteDB

/**
 * @author galou
 * 2019-12-09
 */

/**
 * Convert a [TripWithData] to a [TripWithDataRemoteDB]
 * Trip in the remote Database stored only the id of the watchers instead of the all [User] object
 *
 * @return a [TripWithDataRemoteDB] object with the same data than the TripWithData
 */
fun TripWithData.convertForRemoteDB(): TripWithDataRemoteDB {
    with(this){
        return TripWithDataRemoteDB(
            trip = trip,
            points = points,
            watchersId = watchers.map { it.id }
        )
    }
}

/**
 * Convert a [TripWithDataRemoteDB] to a [TripWithData]
 * Trip in the remote Database stored only the id of the watchers instead of the all [User] object
 *
 * @return a [TripWithData] object with the same data than the TripWithDataRemoteDB
 */
fun TripWithDataRemoteDB.convertForLocal(watchers: List<User>): TripWithData {
    with(this){
        return TripWithData(
            trip = trip,
            points = points,
            watchers = watchers.toMutableList()
        )
    }
}