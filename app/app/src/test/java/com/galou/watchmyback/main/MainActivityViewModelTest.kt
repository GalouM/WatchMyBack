package com.galou.watchmyback.main

import android.app.Activity.RESULT_OK
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.galou.watchmyback.Event
import com.galou.watchmyback.R
import com.galou.watchmyback.data.repository.FakeCheckListRepository
import com.galou.watchmyback.data.repository.FakeFriendRepositoryImpl
import com.galou.watchmyback.data.repository.FakeTripRepositoryImpl
import com.galou.watchmyback.data.repository.FakeUserRepositoryImpl
import com.galou.watchmyback.testHelpers.FakeAuthResult
import com.galou.watchmyback.testHelpers.LiveDataTestUtil
import com.galou.watchmyback.testHelpers.assertSnackBarMessage
import com.galou.watchmyback.utils.RESULT_ACCOUNT_DELETED
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
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
        viewModel = MainActivityViewModel(FakeUserRepositoryImpl(), FakeCheckListRepository(), FakeTripRepositoryImpl(), FakeFriendRepositoryImpl() )
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
        assertThat(userValue.email).isEqualTo(firebaseUser.email)
        assertThat(userValue.username).isEqualTo(firebaseUser.displayName)
        assertThat(userValue.pictureUrl).isEqualTo(firebaseUser.photoUrl)
        assertSnackBarMessage(viewModel.snackbarMessage, R.string.welcome)

    }

    @Test
    fun checkUserIsCreated_AfterSignIn(){
        val firebaseUser = FakeAuthResult.user
        viewModel.handleSignIngActivityResult(RESULT_OK, null, firebaseUser)
        assertNull(LiveDataTestUtil.getValue(viewModel.openSignInActivityEvent))
        val userValue = LiveDataTestUtil.getValue(viewModel.userLD)
        assertThat(userValue.email).isEqualTo(firebaseUser.email)
        assertThat(userValue.username).isEqualTo(firebaseUser.displayName)
        assertThat(userValue.pictureUrl).isEqualTo(firebaseUser.photoUrl)
        assertSnackBarMessage(viewModel.snackbarMessage, R.string.welcome)
    }

    @Test
    fun showModificationSAvedMessage_afterProfileSaved(){
        viewModel.handleResultAfterProfileActivityClosed(RESULT_OK)
        assertSnackBarMessage(viewModel.snackbarMessage,
            R.string.info_updated
        )
    }

    @Test
    fun accountDeletedShowSignInActivity(){
        viewModel.handleResultSettingsActivity(RESULT_ACCOUNT_DELETED)
        val value: Event<Unit> = LiveDataTestUtil.getValue(viewModel.openSignInActivityEvent)
        assertThat(value.getContentIfNotHandled()).isNotNull()
        assertThat(viewModel.userLD.value).isNull()
    }

    @Test
    fun clickMyTripHasActiveTrip_showMyTripActivity(){
        val firebaseUser = FakeAuthResult.user
        viewModel.checkIfUserIsConnected(firebaseUser)
        LiveDataTestUtil.getValue(viewModel.userLD)
        viewModel.showMyTripActivity()
        assertThat(LiveDataTestUtil.getValue(viewModel.openMyTripActivityLD).getContentIfNotHandled()).isNotNull()
    }


}