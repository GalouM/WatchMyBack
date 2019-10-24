package com.galou.watchmyback

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.galou.watchmyback.data.database.dao.UserDao
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.mainActivity.MainActivityViewModel
import com.galou.watchmyback.testHelpers.FakeAuthResult
import com.galou.watchmyback.testHelpers.LiveDataTestUtil
import com.galou.watchmyback.testHelpers.assertSnackBarMessage
import com.google.common.truth.Truth.assertThat
import com.google.firebase.FirebaseApp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

/**
 * Created by galou on 2019-10-24
 */

class MainActivityViewModelTest {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var context: Context

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
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
    }

}