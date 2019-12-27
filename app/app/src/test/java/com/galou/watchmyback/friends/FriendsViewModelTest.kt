package com.galou.watchmyback.friends

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.galou.watchmyback.Event
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.repository.FakeFriendRepositoryImpl
import com.galou.watchmyback.data.repository.FakeUserRepositoryImpl
import com.galou.watchmyback.testHelpers.*
import com.galou.watchmyback.utils.extension.toOtherUser
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    @ExperimentalCoroutinesApi
    @Before
    fun setupViewModel(){
        Dispatchers.setMain(mainThreadSurrogate)
        userRepository = FakeUserRepositoryImpl()
        fakeUser = generateTestUser(TEST_UID)
        userRepository.currentUser.value = fakeUser
        userRepository.userPreferences.value = preferencesTest
        viewModel = FriendsViewModel(userRepository, FakeFriendRepositoryImpl())

    }

    @ExperimentalCoroutinesApi
    @After
    fun close(){
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
        stopKoin()
    }

    @Test
    fun fetch_emitSuccessAndFriends(){
        viewModel.fetchFriends(false)
        assertThat(LiveDataTestUtil.getValue(viewModel.usersLD)).hasSize(2)
        assertThat(LiveDataTestUtil.getValue(viewModel.usersLD)).contains(firstFriend.toOtherUser(true))
        assertThat(LiveDataTestUtil.getValue(viewModel.usersLD)).contains(secondFriend.toOtherUser(true))
    }

    @Test
    fun removeFriend_emitSuccessAndFriends(){
        viewModel.fetchFriends(false)
        LiveDataTestUtil.getValue(viewModel.usersLD)
        viewModel.removeOrAddFriend(firstFriend.toOtherUser(true))
        assertThat(LiveDataTestUtil.getValue(viewModel.usersLD)).hasSize(2)
    }

    @Test
    fun refresh_emitFriendsAndSuccess(){
        viewModel.refreshFriendList()
        assertThat(LiveDataTestUtil.getValue(viewModel.usersLD)).hasSize(2)
    }

    @Test
    fun clickOpenAddFriend_emitOpenAddFriendActivity(){
        viewModel.openAddFriendsActivity()
        val value: Event<Unit> = LiveDataTestUtil.getValue(viewModel.openAddFriendLD)
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

}