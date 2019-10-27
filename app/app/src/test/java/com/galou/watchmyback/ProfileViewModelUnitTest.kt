package com.galou.watchmyback

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.profileActivity.ProfileViewModel
import com.galou.watchmyback.instrumentedTestHelpers.*
import com.galou.watchmyback.testHelpers.LiveDataTestUtil
import com.galou.watchmyback.testHelpers.TEST_UID
import com.galou.watchmyback.testHelpers.UserRepositoryMocked
import com.galou.watchmyback.testHelpers.generateTestUser
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

/**
 * Created by galou on 2019-10-25
 */
class ProfileViewModelUnitTest {
    private lateinit var viewModel: ProfileViewModel
    private lateinit var userRepository: UserRepositoryMocked
    private lateinit var userMocked: User

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
        userRepository = Mockito.mock(UserRepositoryMocked::class.java)
        userMocked =
            generateTestUser(TEST_UID)
        Mockito.`when`(userRepository.currentUser).thenReturn(userMocked)
        viewModel = ProfileViewModel(userRepository)

    }

    @Test
    fun init_emitUserInfo(){
        assertEquals(LiveDataTestUtil.getValue(viewModel.usernameLD), userMocked.username)
        assertEquals(LiveDataTestUtil.getValue(viewModel.phoneNumberLD), userMocked.phoneNumber)
        assertEquals(LiveDataTestUtil.getValue(viewModel.emailLD), userMocked.email)
        assertEquals(LiveDataTestUtil.getValue(viewModel.pictureUrlLD), userMocked.pictureUrl)
    }

    @Test
    fun wrongInfo_showError(){
        viewModel.usernameLD.value = "@incorrect"
        viewModel.emailLD.value = "incorrect@adress"
        viewModel.phoneNumberLD.value = ""
        viewModel.updateUserInformation()
        assertEquals(LiveDataTestUtil.getValue(viewModel.errorEmail), R.string.incorrect_email)
        assertEquals(LiveDataTestUtil.getValue(viewModel.errorPhoneNumber), R.string.incorrect_phone_number)
        assertEquals(LiveDataTestUtil.getValue(viewModel.errorUsername), R.string.incorrect_username)
    }

    /*

    @Test
    fun correctInfo_saveUserAndClose(){
        viewModel.updateUserInformation()
        assertSnackBarMessage(viewModel.snackbarMessage, R.string.info_updated)
        val  saveData: Event<Boolean> = LiveDataTestUtil.getValue(viewModel.dataSaved)
        assertEquals(saveData.getContentIfNotHandled(), true)

    }

     */


}