package com.galou.watchmyback

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.galou.watchmyback.data.database.dao.UserDao
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.mainActivity.MainActivityViewModel
import com.galou.watchmyback.testHelpers.FakeAuthResult
import com.galou.watchmyback.testHelpers.LiveDataTestUtil
import com.galou.watchmyback.testHelpers.assertSnackBarMessage
import com.google.common.truth.Truth.assertThat
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

/**
 * Created by galou on 2019-10-24
 */

@RunWith(RobolectricTestRunner::class)
class MainActivityViewModelTest {

    private lateinit var viewModel: MainActivityViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
        FirebaseApp.initializeApp(getApplicationContext<WatchMyBackApplication>())
        viewModel = MainActivityViewModel(UserRepository())

    }

    @Test
    fun checkUserNotConnected_openSignInActivityEmitted(){
        val firebaseUser = null
        viewModel.checkIfUserIsConnected(firebaseUser)
        val value: Event<Unit> = LiveDataTestUtil.getValue(viewModel.openSignInActivityEvent)
        assertThat(value.getContentIfNotHandled()).isNotNull()

    }

    @Test
    fun checkUserIsConnected_isWelcomeBack(){
        val firebaseUser = FakeAuthResult.user
        viewModel.checkIfUserIsConnected(firebaseUser)
        assertSnackBarMessage(viewModel.snackbarMessage, R.string.welcome_back)
        assertEquals(LiveDataTestUtil.getValue(viewModel.usernameLD), firebaseUser.displayName)
        assertEquals(LiveDataTestUtil.getValue(viewModel.emailLD), firebaseUser.email)
        assertEquals(LiveDataTestUtil.getValue(viewModel.pictureUrlLD), firebaseUser.photoUrl.toString())
    }

}