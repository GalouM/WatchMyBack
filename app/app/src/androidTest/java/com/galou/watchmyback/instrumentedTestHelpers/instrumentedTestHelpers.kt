package com.galou.watchmyback.instrumentedTestHelpers

import android.app.Activity
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.mainActivity.MainActivityViewModel
import com.galou.watchmyback.profileActivity.ProfileViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import org.mockito.Mockito
import java.lang.Exception
import java.util.concurrent.Executor

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
