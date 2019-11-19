package com.galou.watchmyback.addFriend

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.repository.FakeFriendRepositoryImpl
import com.galou.watchmyback.data.repository.FakeUserRepositoryImpl
import com.galou.watchmyback.testHelpers.*
import com.galou.watchmyback.utils.extension.toOtherUser
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.stopKoin

/**
 * @author galou
 * 2019-11-16
 */
class AddFriendViewModelTest {

    private lateinit var viewModel: AddFriendViewModel
    private lateinit var userRepository: FakeUserRepositoryImpl
    private lateinit var fakeUser: User

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
        Dispatchers.setMain(mainThreadSurrogate)
        userRepository = FakeUserRepositoryImpl()
        fakeUser = generateTestUser(TEST_UID)
        userRepository.currentUser.value = fakeUser
        userRepository.userPreferences.value = preferencesTest
        viewModel = AddFriendViewModel(userRepository, FakeFriendRepositoryImpl())

    }

    @After
    fun close(){
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
        stopKoin()
    }

    @Test
    fun init_showAllUsers(){
        assertThat(LiveDataTestUtil.getValue(viewModel.usersLD)).hasSize(2)
        assertThat(LiveDataTestUtil.getValue(viewModel.usersLD)).contains(firstFriend.toOtherUser(true))
        assertThat(LiveDataTestUtil.getValue(viewModel.usersLD)).contains(secondFriend.toOtherUser(true))
    }

    @Test
    fun textEnterAndUsernameSelected_showUserWithUsername(){
        viewModel.searchPattern.value = "firstUser"
        viewModel.changeTypeSearch(FetchType.USERNAME)
        viewModel.fetchUsers()
        assertThat(LiveDataTestUtil.getValue(viewModel.usersLD)).hasSize(1)
        assertThat(LiveDataTestUtil.getValue(viewModel.usersLD)).contains(firstFriend.toOtherUser(true))
    }

    @Test
    fun textEnterAndUEmailSelected_showUserWithEmail(){
        viewModel.searchPattern.value = "seconduser@email.com"
        viewModel.changeTypeSearch(FetchType.EMAIL_ADDRESS)
        viewModel.fetchUsers()
        //assertThat(LiveDataTestUtil.getValue(viewModel.usersLD)).hasSize(1)
        //assertThat(LiveDataTestUtil.getValue(viewModel.usersLD)).contains(secondFriend.toOtherUser(true))
    }
}