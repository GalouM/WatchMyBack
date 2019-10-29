package com.galou.watchmyback

import android.app.Activity.RESULT_OK
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.galou.watchmyback.mainActivity.MainActivityViewModel
import com.galou.watchmyback.testHelpers.*
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by galou on 2019-10-24
 */

class MainActivityViewModelTest {

    private lateinit var viewModel: MainActivityViewModel

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setupViewModel(){
        Dispatchers.setMain(mainThreadSurrogate)
        viewModel = MainActivityViewModel(FakeUserRepositoryImpl())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun checkUserNotConnected_openSignInActivityEmitted(){
        val firebaseUser = null
        viewModel.checkIfUserIsConnected(firebaseUser)
        val value: Event<Unit> = LiveDataTestUtil.getValue(viewModel.openSignInActivityEvent)
        assertThat(value.getContentIfNotHandled()).isNotNull()

    }

    @Test
    fun checkUserIsConnected_isWelcomeBack() = runBlocking {
        val firebaseUser = FakeAuthResult.user
        viewModel.checkIfUserIsConnected(firebaseUser)
        assertNull(LiveDataTestUtil.getValue(viewModel.openSignInActivityEvent))
        val userValue = LiveDataTestUtil.getValue(viewModel.userLD)
        assertEquals(userValue.email, firebaseUser.email)
        assertEquals(userValue.username, firebaseUser.displayName)
        assertEquals(userValue.pictureUrl, firebaseUser.photoUrl)
        assertSnackBarMessage(viewModel.snackbarMessage, R.string.welcome)

    }

    @Test
    fun checkUserIsCreated_AfterSignIn(){
        val firebaseUser = FakeAuthResult.user
        viewModel.handleSignIngActivityResult(RESULT_OK, null, firebaseUser)
        assertNull(LiveDataTestUtil.getValue(viewModel.openSignInActivityEvent))
        val userValue = LiveDataTestUtil.getValue(viewModel.userLD)
        assertEquals(userValue.email, firebaseUser.email)
        assertEquals(userValue.username, firebaseUser.displayName)
        assertEquals(userValue.pictureUrl, firebaseUser.photoUrl)
        assertSnackBarMessage(viewModel.snackbarMessage, R.string.welcome)
    }

    @Test
    fun showModificationSAvedMessage_afterProfileSaved(){
        viewModel.handleResultAfterProfileActivityClosed(RESULT_OK)
        assertSnackBarMessage(viewModel.snackbarMessage, R.string.info_updated)
    }


}