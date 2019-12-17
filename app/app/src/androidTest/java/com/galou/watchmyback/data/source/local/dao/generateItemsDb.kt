package com.galou.watchmyback.data.source.local.dao

import com.galou.watchmyback.data.entity.*
import com.galou.watchmyback.utils.idGenerated
import com.galou.watchmyback.utils.todaysDate

/**
 * Created by galou on 2019-10-22
 */

// users
val mainUser = User(idGenerated, "main-user@gmail.com", "Main User", "5556674567")
val firstFriend = User(idGenerated, "first-friend@gmail.com", "First Friend", "5550985674")
val secondFriend = User(idGenerated, "second-friend@gmail.com", "Second Friend", "5555649834")

// preferences
val mainUserPreferences = UserPreferences(
    mainUser.id, "112", UnitSystem.METRIC, TimeDisplay.H_12,
    notificationBackSafe = false,
    notificationEmergency = true,
    notificationLate = true,
    notificationLocationUpdate = true
)

//friend
val friendship1 = Friendship(mainUser.id, firstFriend.id)
val friendship2 = Friendship(mainUser.id, secondFriend.id)

//check list
val checkList1 = CheckList(idGenerated, mainUser.id, TripType.BIKING, "List 1")
val checkList2 = CheckList(idGenerated, mainUser.id, TripType.HIKING, "List 2")

// items
val item1List1 = ItemCheckList(idGenerated, checkList1.id, "helmet", true)
val item2List1 = ItemCheckList(idGenerated, checkList1.id, "backpack", true)
val item3List1 = ItemCheckList(idGenerated, checkList1.id, "food", false)
val item1List2 = ItemCheckList(idGenerated, checkList2.id, "water bottle", false)
val item2List2 = ItemCheckList(idGenerated, checkList2.id, "backpack", true)

val itemList1 = listOf(
    item1List1,
    item2List1,
    item3List1
)
val itemList2 = listOf(
    item1List2,
    item2List2
)

val checkListWithItem1 = CheckListWithItems(checkList = checkList1, items = itemList1)

//trips
val trip1 = Trip(
    idGenerated, mainUser.id, checkList1.id, TripStatus.ON_GOING, "details about my trip",
    TripUpdateFrequency.FIFTEEN_MINUTES, "Sun Peaks", TripType.BIKING, true
)
val trip2 = Trip(
    idGenerated, mainUser.id, checkList2.id, TripStatus.BACK_SAFE, "details about my other trip",
    TripUpdateFrequency.ONE_HOUR, "Kamloops", TripType.HIKING, false
)

//schedule point
val point1Trip1 = PointTrip(idGenerated, trip1.id, TypePoint.START, todaysDate)
val point2Trip1 = PointTrip(idGenerated, trip1.id, TypePoint.END, todaysDate)
val point3Trip1 = PointTrip(idGenerated, trip1.id, TypePoint.SCHEDULE_STAGE, todaysDate)
val point1Trip2 = PointTrip(idGenerated, trip2.id, TypePoint.START, todaysDate)
val point2Trip2 = PointTrip(idGenerated, trip2.id, TypePoint.END, todaysDate)

val listPoint1 = listOf(
    point1Trip1,
    point2Trip1,
    point3Trip1
)
val listPoint2 = listOf(
    point1Trip2,
    point2Trip2
)

//location
val location1Trip1 = Location(point1Trip1.id, 123.456, -98.654, "Sun peaks", "Canada")
val location2Trip1 = Location(point2Trip1.id, 178.09, -45.987, "Sun peaks", "Canada")
val location3Trip1 = Location(point3Trip1.id, 465.076, -654.675, "Sun peaks", "Canada")
val location1Trip2 = Location(point1Trip2.id, 676.456, -87.654, "Kamloops", "Canada")
val location2Trip2 = Location(point2Trip2.id, 546.09, -56.987, "Kamloops", "Canada")

val listLocationsTrip1 = listOf(
    location1Trip1,
    location2Trip1,
    location3Trip1
)
val listLocationTrip2 = listOf(
    location1Trip2,
    location2Trip2
)

//weather
val weather1Trip1 = WeatherData(idGenerated, point1Trip1.id, WeatherCondition.CLEAR, 200.0, todaysDate, "icon1")
val weather2Trip1 = WeatherData(idGenerated, point2Trip1.id, WeatherCondition.CLOUDS, 300.0, todaysDate, "icon2")
val weather3Trip1 = WeatherData(idGenerated, point3Trip1.id, WeatherCondition.SNOW, 400.0, todaysDate, "icon3")
val weather1Trip2 = WeatherData(idGenerated, point1Trip2.id, WeatherCondition.RAIN, 500.0, todaysDate, "icon4")
val weather2Trip2 = WeatherData(idGenerated, point2Trip2.id, WeatherCondition.MIST, 600.0, todaysDate, "icon5")

val listWeatherTrip1 = listOf(
    weather1Trip1,
    weather2Trip1,
    weather3Trip1
)
val listWeatherTrip2 = listOf(
    weather1Trip2,
    weather2Trip2
)

//watcher
val watcher1Trip1 = TripWatcher(firstFriend.id, trip1.id)
val watcher2Trip1 = TripWatcher(secondFriend.id, trip1.id)
val watcher1Trip2 = TripWatcher(secondFriend.id, trip2.id)

val listWatcherTrip1 = listOf(
    watcher1Trip1,
    watcher2Trip1
)
val listWatcherTrip2 = listOf(watcher1Trip2)

val listPointsWithDataTrip1 = mutableListOf(
    PointTripWithData(pointTrip = point1Trip1, weatherData = weather1Trip1, location = location1Trip1),
    PointTripWithData(pointTrip = point2Trip1, weatherData = weather2Trip1, location = location1Trip1),
    PointTripWithData(pointTrip = point3Trip1, weatherData = weather3Trip1, location = location3Trip1)
)

val listPointWithDataTrip2 = mutableListOf(
    PointTripWithData(pointTrip = point1Trip2, weatherData = weather1Trip2, location = location1Trip2),
    PointTripWithData(pointTrip = point2Trip2, weatherData = weather2Trip2, location = location2Trip2)
)

val tripWithData1 = TripWithData(
    trip = trip1,
    watchers = mutableListOf(firstFriend, secondFriend),
    points = listPointsWithDataTrip1
)

val tripWithData2 = TripWithData(
    trip = trip2,
    watchers = mutableListOf(secondFriend),
    points = listPointWithDataTrip2
)


