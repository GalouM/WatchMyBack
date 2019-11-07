package com.galou.watchmyback.friends

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
 * 2019-11-05
 */
class FriendsViewModelTest {

    private lateinit var viewModel: FriendsViewModel
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
        viewModel = FriendsViewModel(userRepository, FakeFriendRepositoryImpl())

    }

    @After
    fun close(){
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
        stopKoin()
    }

    @Test
    fun init_emitSuccessAndFriends(){
        assertThat(LiveDataTestUtil.getValue(viewModel.friendsLD)).hasSize(2)
        assertThat(LiveDataTestUtil.getValue(viewModel.friendsLD)).contains(firstFriend.toOtherUser(true))
        assertThat(LiveDataTestUtil.getValue(viewModel.friendsLD)).contains(secondFriend.toOtherUser(true))
    }

    @Test
    fun removeFriend_emitSuccessAndFriends(){
        viewModel.removeFriend(firstFriend.toOtherUser(true))
        assertThat(LiveDataTestUtil.getValue(viewModel.friendsLD)).hasSize(2)
    }

    @Test
    fun refresh_emitFriendsAndSuccess(){
        viewModel.refreshFriendList()
        assertThat(LiveDataTestUtil.getValue(viewModel.friendsLD)).hasSize(2)
    }

}