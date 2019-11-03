package com.galou.watchmyback.profile

import android.app.Activity.RESULT_OK
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.net.toUri
import com.galou.watchmyback.Event
import com.galou.watchmyback.R
import com.galou.watchmyback.data.repository.FakeUserRepositoryImpl
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.testHelpers.*
import com.google.common.truth.Truth
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


/**
 * Created by galou on 2019-10-25
 */
@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(RobolectricTestRunner::class)
class ProfileViewModelUnitTest: KoinTest {
    private lateinit var viewModel: ProfileViewModel
    private lateinit var userRepository: FakeUserRepositoryImpl
    private lateinit var fakeUser: User

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
        userRepository = FakeUserRepositoryImpl()
        fakeUser = generateTestUser(TEST_UID)
        userRepository.currentUser.value = fakeUser
        viewModel = ProfileViewModel(userRepository)

    }
    
    @After
    fun close(){
        stopKoin()
    }

    @Test
    fun init_emitUserInfo(){
        assertEquals(LiveDataTestUtil.getValue(viewModel.usernameLD), fakeUser.username)
        assertEquals(LiveDataTestUtil.getValue(viewModel.phoneNumberLD), fakeUser.phoneNumber)
        assertEquals(LiveDataTestUtil.getValue(viewModel.emailLD), fakeUser.email)
        assertEquals(LiveDataTestUtil.getValue(viewModel.pictureUrlLD), fakeUser.pictureUrl)
    }

    @Test
    fun wrongInfo_showError(){
        viewModel.usernameLD.value = "@incorrect"
        viewModel.emailLD.value = "incorrect@adress"
        viewModel.phoneNumberLD.value = ""
        viewModel.updateUserInformation()
        assertEquals(LiveDataTestUtil.getValue(viewModel.errorEmail),
            R.string.incorrect_email
        )
        assertEquals(LiveDataTestUtil.getValue(viewModel.errorPhoneNumber),
            R.string.incorrect_phone_number
        )
        assertEquals(LiveDataTestUtil.getValue(viewModel.errorUsername),
            R.string.incorrect_username
        )
    }


    @Test
    fun correctInfo_saveUserAndClose(){
        viewModel.phoneNumberLD.value = NEW_PHONE_NB
        viewModel.emailLD.value = NEW_EMAIL
        viewModel.usernameLD.value = NEW_USERNAME
        viewModel.updateUserInformation()
        val  saveData: Event<Boolean> = LiveDataTestUtil.getValue(viewModel.dataSaved)
        assertEquals(saveData.getContentIfNotHandled(), true)
        val userUpdated = viewModel.userRepository.currentUser.value
        assertEquals(userUpdated?.username, NEW_USERNAME)
        assertEquals(userUpdated?.email, NEW_EMAIL)
        assertEquals(userUpdated?.phoneNumber, NEW_PHONE_NB)

    }


    @Test
    fun selectNewPicture_updateUserInfo(){
        val newPicturePath = "http://internalUri".toUri()
        viewModel.fetchPicturePickedByUser(RESULT_OK, newPicturePath)
        assertEquals(LiveDataTestUtil.getValue(viewModel.pictureUrlLD), URI_STORAGE_REMOTE)
        val userUpdated = viewModel.userRepository.currentUser.value
        assertEquals(userUpdated?.pictureUrl, URI_STORAGE_REMOTE)
    }

    @Test
    fun clickPicture_OpenPickPhotoIntent(){
        viewModel.pickProfilePicture()
        val openLibrary = LiveDataTestUtil.getValue(viewModel.openPhotoLibrary)
        Truth.assertThat(openLibrary.getContentIfNotHandled()).isNotNull()
    }



}