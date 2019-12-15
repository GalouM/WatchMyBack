package com.galou.watchmyback.utils

import android.Manifest

/**
 * Created by galou on 2019-10-20
 */

// ------ TABLES DATA -------
// tables names
const val USER_TABLE_NAME = "users"
const val TRIP_TABLE_NAME = "trips"
const val FRIEND_TABLE_NAME = "friends"
const val WATCHER_TABLE_NAME = "watchers"
const val POINT_TRIP_TABLE_NAME = "points_trip"
const val LOCATION_TABLE_NAME = "locations"
const val WEATHER_DATA_TABLE_NAME = "weather_data"
const val CHECK_LIST_TABLE_NAME = "check_lists"
const val ITEM_LIST_TABLE_NAME = "items_check_list"
const val PREFERENCES_TABLE_NAME = "user_preferences"
// tables attributes
// user
const val USER_TABLE_UUID = "user_uuid"
const val USER_TABLE_USERNAME = "username"
const val USER_TABLE_EMAIL = "email"
const val USER_TABLE_PHONE_NUMBER = "phone_number"
const val USER_TABLE_PICTURE_URL = "picture_url"
// trip
const val TRIP_TABLE_UUID = "trip_uuid"
const val TRIP_TABLE_TYPE = "trip_type"
const val TRIP_TABLE_FREQUENCY = "update_frequency"
const val TRIP_TABLE_STATUS = "status"
const val TRIP_TABLE_DETAILS = "details"
const val TRIP_TABLE_MAIN_LOCATION = "main location"
const val TRIP_TABLE_CHECK_LIST_UUID = "check_list_id"
const val TRIP_TABLE_USER_UUID = "user_id"
const val TRIP_TABLE_ACTIVE = "active"
// location
const val LOCATION_TABLE_UUID = "location_uuid"
const val LOCATION_TABLE_LATITUDE = "latitude"
const val LOCATION_TABLE_LONGITUDE = "longitude"
const val LOCATION_TABLE_CITY = "city"
const val LOCATION_TABLE_COUNTRY = "country"
// weather
const val WEATHER_DATA_TABLE_UUID = "weather_uuid"
const val WEATHER_DATA_TABLE_CONDITION = "condition"
const val WEATHER_DATA_TABLE_TEMPERATURE = "temperature"
const val WEATHER_DATA_TABLE_POINT_UUID = "point_id"
const val WEATHER_DATA_TABLE_DATETIME = "date_time"
const val WEATHER_DATA_TABLE_ICON = "icon_name"
// item check list
const val ITEM_TABLE_UUID = "item_uuid"
const val ITEM_TABLE_NAME = "name"
const val ITEM_TABLE_LIST_ID = "check_list_id"
const val ITEM_TABLE_CHECKED = "checked"
// check list
const val CHECK_LIST_TABLE_UUID = "default_check_list_uuid"
const val CHECK_LIST_TABLE_TRIP_TYPE = "trip_type"
const val CHECK_LIST_TABLE_USER_UUID = "user_id"
const val CHECK_LIST_TABLE_LIST_NAME = "list_name"
// friend
const val FRIEND_TABLE_USER_UUID = "user_id"
const val FRIEND_TABLE_USER_FRIEND_UUID = "friend_id"
// watcher
const val WATCHER_TABLE_WATCHER_UUID = "watcher_id"
const val WATCHER_TABLE_TRIP_UUID = "trip_id"
// points
const val POINT_TRIP_UUID = "point_uuid"
const val POINT_TRIP_TRIP_UUID = "trip_id"
const val POINT_TRIP_TIME = "time"
const val POINT_TRIP_TYPE = "point_type"
// preferences
const val PREFERENCES_TABLE_USER_UUID = "preferences_user_id"
const val PREFERENCES_TABLE_NOTIFICATION_EMERGENCY_STATUS = "notification_friend_emergency_status"
const val PREFERENCES_TABLE_NOTIFICATION_BACK_SAFE = "notification_friend_back_safe"
const val PREFERENCES_TABLE_NOTIFICATION_LATE = "notification_friend_late"
const val PREFERENCES_TABLE_NOTIFICATION_LOCATION_UPDATE = "notification_friend_update_location"
const val PREFERENCES_TABLE_EMERGENCY_NUMBER = "emergency_number"
const val PREFERENCES_TABLE_UNIT_SYSTEM = "unit_system"
const val PREFERENCES_TABLE_TIME_DISPLAY = "time_display"



// ------ COLLECTION FIRESTORE -------
const val USER_COLLECTION_NAME = "userCollection"
const val CHECKLIST_COLLECTION_NAME = "checkListCollection"
const val TRIP_COLLECTION_NAME = "tripCollection"

// ------ STORAGE REFERENCE -------
const val USER_PICTURE_REFERENCE = "images/userPicture/"

// ------ REQUEST CODE ACTIVITIES -------
const val RC_SIGN_IN = 100
const val RC_LIBRARY_PICK = 200
const val RC_IMAGE_PERMS = 400
const val RC_PROFILE = 500
const val RC_SETTINGS = 600
const val RC_ADD_FRIEND = 700
const val RC_ADD_CHECKLIST = 800
const val RC_LOCATION_PERMS = 900
const val RC_PICK_LOCATION_MAP = 1000

// ------ RESULT CODE ACTIVITIES -------
const val RESULT_ACCOUNT_DELETED = -100
const val RESULT_CHECKLIST_DELETED = -200


// ------ TAG -------
const val TRIP_TYPE_TAG = "tripTypeDialog"
const val UPDATE_HZ_TAG = "updateFrequencyDialog"
const val CHECKLIST_DIALOG = "checklistsDialog"
const val WATCHER_DIALOG = "watcherDialog"

// ------ PERMISSIONS -------
const val PERMS_EXT_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
const val PERMS_LOCALISATION = Manifest.permission.ACCESS_FINE_LOCATION

// ------ ID MAP ---------
const val ICON_LOCATION_ACCENT = "iconLocationAccent"
const val ICON_LOCATION_PRIMARY = "iconLocationPrimary"

// ------ DATA INTENT KEY ---------
const val POINT_LATITUDE = "pointLatitude"
const val POINT_LONGITUDE = "pointLongitude"

