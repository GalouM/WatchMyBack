package com.galou.watchmyback.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.galou.watchmyback.data.source.database.WatchMyBackDatabase
import com.galou.watchmyback.data.source.local.dao.CheckListDao
import com.galou.watchmyback.data.source.local.dao.UserDao
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasSize
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Created by galou on 2019-10-22
 */
@RunWith(AndroidJUnit4::class)
class CheckListDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: WatchMyBackDatabase
    private lateinit var userDao: UserDao
    private lateinit var checkListDao: CheckListDao

    @Before
    fun createDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, WatchMyBackDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        userDao = db.userDao()
        checkListDao = db.checkListDao()

        runBlocking {
            userDao.createUser(mainUser)

            checkListDao.createCheckListAndItems(checkList1, itemList1)
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun createAndGetCheckListFromUser() = runBlocking {
        val checkListFromDB = checkListDao.getUserCheckList(mainUser.id)
        assertThat(checkListFromDB, hasItem(checkList1))
    }

    @Test
    @Throws(Exception::class)
    fun createAndGetCheckListAndItem() = runBlocking {
        val checkListFromDB = checkListDao.getCheckListWithItems(checkList1.id)
        assertEquals(checkListFromDB[0].checkList, checkList1)
        assertThat(checkListFromDB[0].items, hasSize(3))
        assertThat(checkListFromDB[0].items, hasItem(item1List1))
    }

    @Test
    @Throws(Exception::class)
    fun updateCheckListAndItem() = runBlocking {
        item1List1.name = "snack"
        checkList1.name = "My awesome list"

        checkListDao.updateCheckListAndItems(checkList1, itemList1)

        val listAndItemFromDB = checkListDao.getCheckListWithItems(checkList1.id)
        val item1FromList = listAndItemFromDB[0].items.find { it.id == item1List1.id }
        assertEquals(item1List1.name, item1FromList?.name)
        assertEquals(checkList1.name, listAndItemFromDB[0].checkList.name)

    }

    @Test
    @Throws(Exception::class)
    fun deleteCheckList() = runBlocking {
        checkListDao.deleteCheckList(checkList1.id)
        val checkListFromDB = checkListDao.getCheckListWithItems(checkList1.id)
        assertThat(checkListFromDB, hasSize(0))
    }

}