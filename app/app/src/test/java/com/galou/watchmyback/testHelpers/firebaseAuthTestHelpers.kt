package com.galou.watchmyback.testHelpers

import android.os.Parcel
import com.google.firebase.auth.AdditionalUserInfo
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import org.mockito.Mockito

/**
 * Created by galou on 2019-10-24
 */
object FakeAuthResult : AuthResult {
    private var user: FirebaseUser? = null

    override fun getAdditionalUserInfo(): AdditionalUserInfo =
        FakeAdditionalUserInfo

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        throw IllegalStateException("Don't try to parcel FakeAuthResult!")
    }

    override fun getUser(): FirebaseUser {
        return getFakeFirebaseUser()
    }

    override fun describeContents(): Int = 0
}

object FakeAdditionalUserInfo : AdditionalUserInfo {

    override fun getUsername(): String? = null

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        throw IllegalStateException("Don't try to parcel FakeAuthResult!")
    }

    override fun getProfile(): MutableMap<String, Any>? = null

    override fun getProviderId(): String? = null

    override fun describeContents(): Int = 0

    override fun isNewUser(): Boolean = false
}

fun getFakeFirebaseUser(): FirebaseUser{
    val user = Mockito.mock(FirebaseUser::class.java)
    val testUser =
        generateTestUser(TEST_UID)
    Mockito.`when`(user.uid).thenReturn(testUser.id)
    Mockito.`when`(user.email).thenReturn(testUser.email)
    Mockito.`when`(user.displayName).thenReturn(testUser.username)
    Mockito.`when`(user.photoUrl).thenReturn(null)
    Mockito.`when`(user.phoneNumber).thenReturn(testUser.phoneNumber)
    return user
}



