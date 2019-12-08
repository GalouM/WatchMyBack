package com.galou.watchmyback.checkList

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.galou.watchmyback.Event
import com.galou.watchmyback.checklist.CheckListViewModel
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.repository.FakeCheckListRepository
import com.galou.watchmyback.data.repository.FakeUserRepositoryImpl
import com.galou.watchmyback.testHelpers.*
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
 * 2019-11-27
 */
class CheckListViewModelTest {
    private lateinit var viewModel: CheckListViewModel
    private lateinit var userRepository: FakeUserRepositoryImpl
    private lateinit var fakeUser: User
    private lateinit var checkListRepository: FakeCheckListRepository

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
        checkListRepository = FakeCheckListRepository()
        viewModel = CheckListViewModel(userRepository, checkListRepository)

    }

    @After
    fun close(){
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
        stopKoin()
    }

    @Test
    fun init_showUserCheckLists(){
        assertThat(LiveDataTestUtil.getValue(viewModel.checkListLD)).hasSize(2)
        assertThat(checkListRepository.checkListFetched).isTrue()
    }

    @Test
    fun refresh_showChecklists(){
        viewModel.refresh()
        assertThat(LiveDataTestUtil.getValue(viewModel.checkListLD)).hasSize(2)
    }

    @Test
    fun createCheckList_emitCreateLD(){
        viewModel.addCheckList()
        val value: Event<Unit> = LiveDataTestUtil.getValue(viewModel.openAddModifyCheckList)
        assertThat(value.getContentIfNotHandled()).isNotNull()
        assertThat(checkListRepository.checkList).isNull()
    }

    @Test
    fun modifyCheckList_emitModify(){
        viewModel.modifyCheckList(checkListWithItem1)
        val value: Event<Unit> = LiveDataTestUtil.getValue(viewModel.openAddModifyCheckList)
        assertThat(value.getContentIfNotHandled()).isNotNull()
        assertThat(checkListRepository.checkList).isEqualTo(checkList1)
    }
}