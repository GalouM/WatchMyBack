package com.galou.watchmyback.addTrip

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.TripType
import com.galou.watchmyback.data.entity.TripUpdateFrequency
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.repository.FakeCheckListRepository
import com.galou.watchmyback.data.repository.FakeFriendRepositoryImpl
import com.galou.watchmyback.data.repository.FakeUserRepositoryImpl
import com.galou.watchmyback.data.repository.FriendRepository
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
 * 2019-12-01
 */
class AddTripViewModelTest {

    private lateinit var viewModel: AddTripViewModel
    private lateinit var userRepository: FakeUserRepositoryImpl
    private lateinit var fakeUser: User
    private lateinit var checkListRepository: FakeCheckListRepository
    private lateinit var friendRepository: FriendRepository

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
        friendRepository = FakeFriendRepositoryImpl()
        viewModel = AddTripViewModel(friendRepository, userRepository, checkListRepository)

    }

    @After
    fun close(){
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
        stopKoin()
    }

    @Test
    fun clickType_emitTypesPossible(){
        viewModel.showTripTypeDialog()
        val content = LiveDataTestUtil.getValue(viewModel.typesTrip).getContentIfNotHandled()
        assertThat(content).isNotNull()
        assertThat(content).containsExactlyElementsIn(TripType.values())
    }

    @Test
    fun clickFrequency_emitTypesPossible(){
        viewModel.showTripUpdateFrequency()
        val content = LiveDataTestUtil.getValue(viewModel.updateHzTripLD).getContentIfNotHandled()
        assertThat(content).isNotNull()
        assertThat(content).containsExactlyElementsIn(TripUpdateFrequency.values())
    }

    @Test
    fun clickWatchers_emitListFriends(){
        viewModel.showFriendsList()
        val value = LiveDataTestUtil.getValue(viewModel.friendsLD)
        assertThat(value.getContentIfNotHandled()).isNotNull()

    }

    @Test
    fun onWatcherSelected_listWatcherUpdated(){
        val listOfWatchers = listOf(firstFriend, secondFriend)
        viewModel.selectWatchers(listOfWatchers)
        assertThat(LiveDataTestUtil.getValue(viewModel.watchersLD)).containsExactlyElementsIn(listOfWatchers)
    }

    @Test
    fun clickPickCheckListWithNoType_showSnackbar(){
        viewModel.clickPickCheckList()
        assertSnackBarMessage(viewModel.snackbarMessage, R.string.select_type_first)
    }

    @Test
    fun clickPickCheckListType_emitCheckList(){
        viewModel.tripLD.value?.type = TripType.HIKING
        viewModel.clickPickCheckList()
        val value = LiveDataTestUtil.getValue(viewModel.openPickCheckListLD)
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun selectCheckList_setTripCheckList(){
        viewModel.selectCheckList(checkListWithItem1)
        assertThat(LiveDataTestUtil.getValue(viewModel.itemCheckListLD))
            .containsExactlyElementsIn(checkListWithItem1.items)
        assertThat(LiveDataTestUtil.getValue(viewModel.tripLD).checkListId)
            .isEqualTo(checkListWithItem1.checkList.id)
    }

    @Test
    fun selectFrequency_updateTripFrequency(){
        viewModel.selectUpdateFrequency(TripUpdateFrequency.FIFTEEN_MINUTES)
        assertThat(LiveDataTestUtil.getValue(viewModel.tripLD).updateFrequency)
            .isEqualTo(TripUpdateFrequency.FIFTEEN_MINUTES)
    }

    @Test
    fun selectType_updateTripType(){
        viewModel.selectTripType(TripType.HIKING)
        assertThat(LiveDataTestUtil.getValue(viewModel.tripLD).type)
            .isEqualTo(TripType.HIKING)
    }

    @Test
    fun clickAddCheckList_emitOpenAddCheckListActivity(){
        viewModel.clickAddCheckList()
        val value = LiveDataTestUtil.getValue(viewModel.openAddCheckListLD)
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun clickAddFriend_emitOpenAddFriendActivity(){
        viewModel.clickAddFriends()
        val value = LiveDataTestUtil.getValue(viewModel.openAddFriendLD)
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun clickAddStagePoint_addPointInList(){
        viewModel.addStagePoint()
        var points = LiveDataTestUtil.getValue(viewModel.stagePointsLD)
        println(points)
        val pointsSize = points.size
        assertThat(points).isNotNull()
        viewModel.addStagePoint()
        points = LiveDataTestUtil.getValue(viewModel.stagePointsLD)
        assertThat(points).hasSize(pointsSize + 1)

    }


}