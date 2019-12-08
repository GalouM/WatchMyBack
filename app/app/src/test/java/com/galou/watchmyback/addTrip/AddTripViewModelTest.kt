package com.galou.watchmyback.addTrip

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.*
import com.galou.watchmyback.data.repository.FakeCheckListRepository
import com.galou.watchmyback.data.repository.FakeFriendRepositoryImpl
import com.galou.watchmyback.data.repository.FakeUserRepositoryImpl
import com.galou.watchmyback.data.repository.FriendRepository
import com.galou.watchmyback.testHelpers.*
import com.galou.watchmyback.utils.todaysDate
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
        val watcher = Watcher(firstFriend, true)
        viewModel.addRemoveWatcher(watcher)
        assertThat(LiveDataTestUtil.getValue(viewModel.watchersLD)).contains(firstFriend)
        watcher.watchTrip = false
        viewModel.addRemoveWatcher(watcher)
        assertThat(LiveDataTestUtil.getValue(viewModel.watchersLD)).doesNotContain(firstFriend)
    }

    @Test
    fun clickPickCheckListWithNoType_showSnackbar(){
        viewModel.showCheckLists()
        assertSnackBarMessage(viewModel.snackbarMessage, R.string.select_type_first)
    }

    @Test
    fun clickPickCheckListType_emitCheckList(){
        viewModel.tripLD.value?.type = TripType.HIKING
        viewModel.showCheckLists()
        val value = LiveDataTestUtil.getValue(viewModel.openPickCheckListLD)
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun selectCheckList_setTripCheckList(){
        viewModel.selectCheckList(checkListWithItem1)
        val items = LiveDataTestUtil.getValue(viewModel.itemCheckListLD)
        assertThat(items).containsExactlyElementsIn(checkListWithItem1.items)
        assertThat(LiveDataTestUtil.getValue(viewModel.tripLD).checkListId)
            .isEqualTo(checkListWithItem1.checkList.id)
        items.forEach { assertThat(it.checked).isFalse() }

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
        viewModel.openAddCheckListActivity()
        val value = LiveDataTestUtil.getValue(viewModel.openAddCheckListLD)
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun clickAddFriend_emitOpenAddFriendActivity(){
        viewModel.openAddFriendsActivity()
        val value = LiveDataTestUtil.getValue(viewModel.openAddFriendLD)
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun clickAddStagePoint_addPointInList(){
        viewModel.addStagePoint()
        var points = LiveDataTestUtil.getValue(viewModel.stagePointsLD)
        val pointsSize = points.size
        assertThat(points).isNotNull()
        viewModel.addStagePoint()
        points = LiveDataTestUtil.getValue(viewModel.stagePointsLD)
        assertThat(points).hasSize(pointsSize + 1)

    }

    @Test
    fun clickStagePointFromMap_openMapActivity(){
        val point = PointTripWithData(pointTrip = PointTrip())
        viewModel.setPointFromMap(point)
        val content = LiveDataTestUtil.getValue(viewModel.openMapLD).getContentIfNotHandled()
        assertThat(content).isNotNull()
        assertThat(content).isEqualTo(point)
    }

    @Test
    fun clickStartPointFromMap_openMapActivity(){
        viewModel.setPointFromMap(R.id.add_trip_start_point_pick)
        val content = LiveDataTestUtil.getValue(viewModel.openMapLD).getContentIfNotHandled()
        assertThat(content).isNotNull()
        assertThat(content?.pointTrip?.typePoint).isEqualTo(TypePoint.START)

    }

    @Test
    fun clickEndPointFromMap_openMapActivity(){
        viewModel.setPointFromMap(R.id.add_trip_end_point_pick)
        val content = LiveDataTestUtil.getValue(viewModel.openMapLD).getContentIfNotHandled()
        assertThat(content).isNotNull()
        assertThat(content?.pointTrip?.typePoint).isEqualTo(TypePoint.END)
    }

    @Test
    fun updatePointLocation_changeLocationPointInList(){
        viewModel.addStagePoint()
        val pointToUpdate = LiveDataTestUtil.getValue(viewModel.stagePointsLD)[0]
        val lat = 10.10
        val lgn = 20.20
        viewModel.setPointLocation(lat, lgn, pointToUpdate)

        val pointWithNewData = LiveDataTestUtil.getValue(viewModel.stagePointsLD)[0]
        assertThat(pointWithNewData.location?.latitude).isEqualTo(lat)
        assertThat(pointWithNewData.location?.longitude).isEqualTo(lgn)
    }

    @Test
    fun updateStartPointLocation_changeLocationStartPoint(){
        val startPoint = LiveDataTestUtil.getValue(viewModel.startPointLD)
        val lat = 123.456
        val lgn = 89.098
        viewModel.setPointLocation(lat, lgn, startPoint)

        val startPointWithNewData = LiveDataTestUtil.getValue(viewModel.startPointLD)
        assertThat(startPointWithNewData.location?.latitude).isEqualTo(lat)
        assertThat(startPointWithNewData.location?.longitude).isEqualTo(lgn)

    }

    @Test
    fun onSaveTripWithFieldsEmptyAndNoStagePoint_showError(){
        viewModel.startTrip()
        assertThat(LiveDataTestUtil.getValue(viewModel.typeError)).isNotNull()
        assertThat(LiveDataTestUtil.getValue(viewModel.updateFrequencyError)).isNotNull()
        assertThat(LiveDataTestUtil.getValue(viewModel.watchersLD)).isNotNull()
        assertThat(LiveDataTestUtil.getValue(viewModel.startPointLatError)).isNotNull()
        assertThat(LiveDataTestUtil.getValue(viewModel.startPointLngError)).isNotNull()
        assertThat(LiveDataTestUtil.getValue(viewModel.startPointTimeError)).isNotNull()
        assertThat(LiveDataTestUtil.getValue(viewModel.endPointLatError)).isNotNull()
        assertThat(LiveDataTestUtil.getValue(viewModel.endPointLngError)).isNotNull()
        assertThat(LiveDataTestUtil.getValue(viewModel.endPointTimeError)).isNotNull()
        assertThat(LiveDataTestUtil.getValue(viewModel.dataLoading)).isFalse()
    }

    @Test
    fun onSaveTripWithStagePointFieldEmpty_removeStage(){
        viewModel.addStagePoint()
        val point1 = LiveDataTestUtil.getValue(viewModel.stagePointsLD)[0]
        viewModel.addStagePoint()
        val point2 = LiveDataTestUtil.getValue(viewModel.stagePointsLD)[1]
        viewModel.setPointLocation(123.344, 567.432, point1)
        viewModel.startTrip()
        val points = LiveDataTestUtil.getValue(viewModel.stagePointsLD)
        assertThat(points).contains(point1)
        assertThat(points).doesNotContain(point2)
    }

    @Test
    fun noMainLocationEntered_setLocationFromCityStartPoint(){
        val city = "Sun Peaks"
        val country = "Canada"
        with(viewModel.tripLD.value!!){
            type = TripType.HIKING
            updateFrequency = TripUpdateFrequency.FIFTEEN_MINUTES
        }
        with(viewModel.startPointLD.value!!){
            location?.city = city
            location?.country = country
            location?.latitude = 123.455
            location?.longitude = 123.453
            pointTrip.time = todaysDate
        }
        with(viewModel.endPointLD.value!!){
            location?.latitude = 123.455
            location?.longitude = 123.453
            pointTrip.time = todaysDate
        }
        viewModel.addRemoveWatcher(Watcher(firstFriend, true))
        viewModel.startTrip()
        assertThat(LiveDataTestUtil.getValue(viewModel.tripLD).mainLocation).isEqualTo(city)

    }

    @Test
    fun noMainLocationEntered_setLocationFromCountryStartPoint(){
        val country = "Canada"
        with(viewModel.tripLD.value!!){
            type = TripType.HIKING
            updateFrequency = TripUpdateFrequency.FIFTEEN_MINUTES
        }
        with(viewModel.startPointLD.value!!){
            location?.country = country
            location?.latitude = 123.455
            location?.longitude = 123.453
            pointTrip.time = todaysDate
        }
        with(viewModel.endPointLD.value!!){
            location?.latitude = 123.455
            location?.longitude = 123.453
            pointTrip.time = todaysDate
        }
        viewModel.addRemoveWatcher(Watcher(firstFriend, true))
        viewModel.startTrip()
        viewModel.startTrip()
        assertThat(LiveDataTestUtil.getValue(viewModel.tripLD).mainLocation).isEqualTo(country)
    }

    @Test
    fun clickSetTimeFromStartPoint_openTimePickerWithRightData(){
        val buttonClicked = R.id.add_trip_start_point_time_text
        val userPref = userRepository.userPreferences.value
        viewModel.showTimePicker(buttonClicked)
        val value = LiveDataTestUtil.getValue(viewModel.openTimePickerLD).getContentIfNotHandled()
        assertThat(value).isNotNull()
        assertThat(value?.values?.first()).isEqualTo(LiveDataTestUtil.getValue(viewModel.startPointLD))
        assertThat(value?.keys?.first()).isEqualTo(userPref?.timeDisplay)
    }

    @Test
    fun clickSetTimeFromEndPoint_openTimePickerWithRightData(){
        val buttonClicked = R.id.add_trip_end_point_time_text
        val userPref = userRepository.userPreferences.value
        viewModel.showTimePicker(buttonClicked)
        val value = LiveDataTestUtil.getValue(viewModel.openTimePickerLD).getContentIfNotHandled()
        assertThat(value).isNotNull()
        assertThat(value?.values?.first()).isEqualTo(LiveDataTestUtil.getValue(viewModel.endPointLD))
        assertThat(value?.keys?.first()).isEqualTo(userPref?.timeDisplay)
    }


    @Test
    fun clickSetTimeFromStagePoint_openTimePickerWithRightData(){
        viewModel.addStagePoint()
        val point = LiveDataTestUtil.getValue(viewModel.stagePointsLD)[0]
        val userPref = userRepository.userPreferences.value
        viewModel.showTimePicker(point)
        val value = LiveDataTestUtil.getValue(viewModel.openTimePickerLD).getContentIfNotHandled()
        assertThat(value).isNotNull()
        assertThat(value?.values?.first()).isEqualTo(point)
        assertThat(value?.keys?.first()).isEqualTo(userPref?.timeDisplay)
    }




}