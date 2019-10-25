package com.galou.watchmyback.testHelpers

import android.os.Parcel
import com.google.firebase.auth.AdditionalUserInfo
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import org.mockito.Mockito
import java.lang.IllegalStateException

/**
 * Created by galou on 2019-10-24
 */
object FakeAuthResult : AuthResult {
    private var user: FirebaseUser? = null

    override fun getAdditionalUserInfo(): AdditionalUserInfo = FakeAdditionalUserInfo

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        throw IllegalStateException("Don't try to parcel FakeAuthResult!")
    }

    override fun getUser(): FirebaseUser {
        return getMockFirebaseUser()
    }

    override fun describeContents(): Int = 0
}

object FakeAdditionalUserInfo : AdditionalUserInfo {

    override fun getUsername(): String? = null

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        throw IllegalStateException("Don't try to parcel FakeAuthResult!");
    }

    override fun getProfile(): MutableMap<String, Any>? = null

    override fun getProviderId(): String? = null

    override fun describeContents(): Int = 0

    override fun isNewUser(): Boolean = false
}

fun getMockFirebaseUser(): FirebaseUser{
    val user = Mockito.mock(FirebaseUser::class.java)
    val testUser = generateTestUser(TEST_UID)
    Mockito.`when`(user.uid).thenReturn(testUser.id)
    Mockito.`when`(user.email).thenReturn(testUser.email)
    Mockito.`when`(user.displayName).thenReturn(testUser.username)
    Mockito.`when`(user.photoUrl).thenReturn(null)
    Mockito.`when`(user.phoneNumber).thenReturn(testUser.phoneNumber)
    return user
}

const val TEST_UID = "uid"
const val TEST_EMAIL = "test@example.com"
const val TEST_NAME = "John Doe"
const val TEST_PHOTO_URI = "http://example.com/profile.png"
const val TEST_PHONE_NUMBER = "555-456-3454"


