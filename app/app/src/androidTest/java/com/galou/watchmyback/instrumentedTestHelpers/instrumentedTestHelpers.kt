package com.galou.watchmyback.instrumentedTestHelpers

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import com.galou.watchmyback.data.entity.TimeDisplay
import com.galou.watchmyback.data.entity.UnitSystem
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.entity.UserPreferences
import com.google.firebase.auth.FirebaseUser
import org.hamcrest.Matcher
import org.mockito.Mockito

/**
 * Created by galou on 2019-10-27
 */
fun generateTestUser(id: String) = User(
    id,
    TEST_EMAIL,
    TEST_NAME,
    TEST_PHONE_NUMBER,
    TEST_PHOTO_URI
)

const val TEST_UID = "uid"
const val TEST_EMAIL = "test@example.com"
const val TEST_NAME = "John Doe"
const val TEST_PHOTO_URI = "http://example.com/profile.png"
const val TEST_PHONE_NUMBER = "555-456-3454"

const val TEST_EMERGENCY_NUMBER = "911"
val TEST_TIME_DISPLAY = TimeDisplay.H_24
val TEST_UNIT_SYSTEM = UnitSystem.METRIC
const val TEST_NOTIFICATION_BACK = true
const val TEST_NOTIFICATION_EMERGENCY = false
const val TEST_NOTIFICATION_UPDATE = true
const val TEST_NOTIFICATION_LATE = true

fun getFakeFirebaseUser(): FirebaseUser {
    val user = Mockito.mock(FirebaseUser::class.java)
    val testUser = generateTestUser(TEST_UID)
    Mockito.`when`(user.uid).thenReturn(testUser.id)
    Mockito.`when`(user.email).thenReturn(testUser.email)
    Mockito.`when`(user.displayName).thenReturn(testUser.username)
    Mockito.`when`(user.photoUrl).thenReturn(null)
    Mockito.`when`(user.phoneNumber).thenReturn(testUser.phoneNumber)
    return user
}

val preferencesTest = UserPreferences(
    id = TEST_UID,
    emergencyNumber = TEST_EMERGENCY_NUMBER,
    timeDisplay = TEST_TIME_DISPLAY,
    unitSystem = TEST_UNIT_SYSTEM,
    notificationBackSafe = TEST_NOTIFICATION_BACK,
    notificationLate = TEST_NOTIFICATION_LATE

)

fun hasCorrectValue(value: Boolean): Matcher<View> {
    return when(value){
        true -> isChecked()
        false -> isNotChecked()
    }

}


