package com.galou.watchmyback.addModifyCheckList

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.galou.watchmyback.Event
import com.galou.watchmyback.data.entity.ItemCheckList
import com.galou.watchmyback.data.entity.TripType
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
class AddModifyCheckListViewModelTest {

    private lateinit var viewModel: AddModifyCheckListViewModel
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
        checkListRepository.checkList = checkList1
        viewModel = AddModifyCheckListViewModel(checkListRepository, userRepository)

    }

    @After
    fun close(){
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
        stopKoin()
    }

    @Test
    fun initFromModify_showCheckListInformation(){
        assertThat(LiveDataTestUtil.getValue(viewModel.checkListLD)).isEqualTo(checkList1)
        assertThat(LiveDataTestUtil.getValue(viewModel.modifyCheckList)).isTrue()
        assertThat(LiveDataTestUtil.getValue(viewModel.itemsCheckListLD)).isEqualTo(itemList1)
    }

    @Test
    fun initFromAdd_showEmptyFields(){
        checkListRepository.checkList = null
        viewModel = AddModifyCheckListViewModel(checkListRepository, userRepository)
        assertThat(LiveDataTestUtil.getValue(viewModel.modifyCheckList)).isFalse()
        val items = LiveDataTestUtil.getValue(viewModel.itemsCheckListLD)
        assertThat(items).isEmpty()
        val checkList = LiveDataTestUtil.getValue(viewModel.checkListLD)
        assertThat(checkList.name).isEmpty()
        assertThat(checkList.tripType).isNull()
        assertThat(checkList.userId).isEqualTo(fakeUser.id)
    }

    @Test
    fun clickType_emitTypesPossible(){
        viewModel.showCheckListTypeDialog()
        val value: Event<List<TripType>> = LiveDataTestUtil.getValue(viewModel.typesCheckList)
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun saveExistingCheckList_saveCheckList(){
        LiveDataTestUtil.getValue(viewModel.checkListLD)
        LiveDataTestUtil.getValue(viewModel.itemsCheckListLD)
        viewModel.saveCheckList()
        val value = LiveDataTestUtil.getValue(viewModel.checkListSavedLD)
        assertThat(value.getContentIfNotHandled()).isNotNull()
        assertThat(LiveDataTestUtil.getValue(viewModel.errorNameLD)).isNull()
        assertThat(LiveDataTestUtil.getValue(viewModel.errorTypeLD)).isNull()

    }

    @Test
    fun saveNewCheckList_saveCheckList(){
        checkListRepository.checkList = null
        viewModel = AddModifyCheckListViewModel(checkListRepository, userRepository)
        val checkList = LiveDataTestUtil.getValue(viewModel.checkListLD)
        checkList.name = "new name"
        checkList.tripType = TripType.BIKING
        val items = LiveDataTestUtil.getValue(viewModel.itemsCheckListLD)
        items.add(ItemCheckList(name = "My item"))
        viewModel.saveCheckList()
        val value = LiveDataTestUtil.getValue(viewModel.checkListSavedLD)
        assertThat(value.getContentIfNotHandled()).isNotNull()
        assertThat(LiveDataTestUtil.getValue(viewModel.errorNameLD)).isNull()
        assertThat(LiveDataTestUtil.getValue(viewModel.errorTypeLD)).isNull()
    }

    @Test
    fun clickDelete_deleteCheckList(){
        viewModel.deleteCheckList()
        val value: Event<Unit> = LiveDataTestUtil.getValue(viewModel.checkListDeletedLD)
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun selectType_changeTypeCheckList(){
        val tripType = TripType.HIKING
        viewModel.selectCheckListType(tripType)
        val checkList = LiveDataTestUtil.getValue(viewModel.checkListLD)
        assertThat(checkList.tripType).isEqualTo(tripType)
    }

    @Test
    fun addItem_addItemToList(){
        val items = LiveDataTestUtil.getValue(viewModel.itemsCheckListLD)
        val numberItems = items.size
        viewModel.addItem()
        assertThat(items.size).isEqualTo(numberItems + 1)
    }

    @Test
    fun removeItem_removeItemToList(){
        val items = LiveDataTestUtil.getValue(viewModel.itemsCheckListLD)
        val numberItems = items.size
        viewModel.removeItem(items[0])
        assertThat(items.size).isEqualTo(numberItems - 1)
    }

    @Test
    fun noName_showError(){
        val checkList = LiveDataTestUtil.getValue(viewModel.checkListLD)
        LiveDataTestUtil.getValue(viewModel.itemsCheckListLD)
        checkList.name = ""
        viewModel.saveCheckList()
        assertThat(LiveDataTestUtil.getValue(viewModel.errorNameLD)).isNotNull()
    }

    @Test
    fun noType_showError(){
        val checkList = LiveDataTestUtil.getValue(viewModel.checkListLD)
        LiveDataTestUtil.getValue(viewModel.itemsCheckListLD)
        checkList.tripType = null
        viewModel.saveCheckList()
        assertThat(LiveDataTestUtil.getValue(viewModel.errorTypeLD)).isNotNull()
    }

    @Test
    fun noItems_showError(){
        val items = LiveDataTestUtil.getValue(viewModel.itemsCheckListLD)
        items.clear()
        viewModel.saveCheckList()
        val value = LiveDataTestUtil.getValue(viewModel.snackbarMessage)
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun addItemWithNoName_deleteItemOnSave(){
        val items = LiveDataTestUtil.getValue(viewModel.itemsCheckListLD)
        val numberItems = items.size
        viewModel.addItem()
        assertThat(LiveDataTestUtil.getValue(viewModel.itemsCheckListLD)).hasSize(numberItems + 1)
        viewModel.saveCheckList()
        assertThat(LiveDataTestUtil.getValue(viewModel.itemsCheckListLD)).hasSize(numberItems)
    }


}