package com.galou.watchmyback.data.entity

/**
 * @author galou
 * 2019-12-05
 */

/**
 * Use to display a list of possible watcher
 * Hold a user information and if it is set as a watcher for the current trip or not
 *
 * @property user user's information
 * @property watchTrip boolean determine if the user is watching the trip or not
 */
data class Watcher(
    val user: User,
    var watchTrip: Boolean
)