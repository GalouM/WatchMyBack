package com.galou.watchmyback.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.galou.watchmyback.data.entity.CheckList
import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.data.entity.ItemCheckList
import com.galou.watchmyback.data.source.database.WatchMyBackDatabase
import com.galou.watchmyback.data.source.local.dao.*
import com.galou.watchmyback.utils.Result
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * @author galou
 * 2019-11-20
 */
@MediumTest
@RunWith(AndroidJUnit4::class)
class CheckListLocalSourceTests {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: WatchMyBackDatabase
    private lateinit var localSource: CheckListLocalDataSource
    private lateinit var userDao: UserDao
    private lateinit var checkListDao: CheckListDao

    @Before
    fun createDatabase(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, WatchMyBackDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        userDao = db.userDao()
        checkListDao = db.checkListDao()
        localSource = CheckListLocalDataSource(checkListDao)

        runBlocking {
            userDao.createUser(mainUser)
            checkListDao.createCheckListAndItems(checkList1, itemList1)
            checkListDao.createCheckListAndItems(checkList2, itemList2)
            checkListDao.createCheckList(CheckList())
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase(){
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun fetchUserCheckList_returnCurrentUserCheckList() = runBlocking {
        val task = localSource.fetchUserCheckLists(mainUser.id)

        //check operation was successful
        val result = task as? Result.Success
        assertThat(result, `is` (notNullValue()))

        assertThat(result!!.data.size, equalTo(2))
        assertThat(result.data, hasItem(checkList1))
        assertThat(result.data, hasItem(checkList2))
    }

    @Test
    @Throws(Exception::class)
    fun fetchCheckList_returnCheckListWithCorrectItems() = runBlocking {
        val task = localSource.fetchCheckList(checkList1.id)
        val result = task as? Result.Success

        //check operation was successful
        assertThat(result, `is` (notNullValue()))

        assertThat(result?.data?.items, equalTo(itemList1))
        assertThat(result?.data?.checkList, equalTo(checkList1))
    }

    @Test
    @Throws(Exception::class)
    fun createCheckList_createCheckListAndItemsInDB() = runBlocking {
        val newCheckList = CheckListWithItems(
            checkList = CheckList(id = "myUuid", name = "new checklist"),
            items = listOf(ItemCheckList("1"), ItemCheckList("2"))
        )
        val task = localSource.createCheckList(newCheckList)

        //check operation was successful
        val result = task as? Result.Success
        assertThat(result, `is` (notNullValue()))

        val listFromDB = checkListDao.getCheckListWithItems(newCheckList.checkList.id)

        assertThat(listFromDB, equalTo(newCheckList))

    }

    @Test
    @Throws(Exception::class)
    fun updateCheckList_UpdateInDB() = runBlocking {
        val newName = "new name"
        checkList1.name = newName
        item1List1.name = newName

        val task = localSource.updateCheckList(checkList1, itemList1)

        //check operation was successful
        val result = task as? Result.Success
        assertThat(result, `is` (notNullValue()))

        val listFromDB = checkListDao.getCheckListWithItems(checkList1.id)

        assertThat(listFromDB?.checkList?.name, `is` (newName))

        val modifiedItem = listFromDB?.items?.find { it.id == item1List1.id }
        assertThat(modifiedItem?.name, `is` (newName))

    }

    @Test
    @Throws(Exception::class)
    fun deleteCheckList_deleteInDB() = runBlocking {
        val checkList = CheckListWithItems(
            checkList1, itemList1
        )
        val task = localSource.deleteCheckList(checkList)
        //check operation was successful
        val result = task as? Result.Success
        assertThat(result, `is` (notNullValue()))

        val listFromDB = checkListDao.getCheckListWithItems(checkList1.id)
        assertThat(listFromDB, `is` (nullValue()))

    }

    @Test
    @Throws(Exception::class)
    fun deleteAllCheckLists_deleteInDB() = runBlocking {
        val task = localSource.deleteExistingChecklist(mainUser.id)
        //check operation was successful
        val result = task as? Result.Success
        assertThat(result, `is` (notNullValue()))

        val listsFromDB = checkListDao.getUserCheckList(mainUser.id)
        assertThat(listsFromDB, hasSize(0))

    }


}