package com.galou.watchmyback.addModifyCheckList

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.galou.watchmyback.R
import com.galou.watchmyback.data.repository.CheckListRepository
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.data.source.local.dao.checkList1
import com.galou.watchmyback.instrumentedTestHelpers.TEST_UID
import com.galou.watchmyback.instrumentedTestHelpers.generateTestUser
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito

/**
 * @author galou
 * 2019-11-27
 */
@MediumTest
@RunWith(AndroidJUnit4::class)
class AddModifyCheckListActivityTest : KoinTest {

    private val user = generateTestUser(TEST_UID)

    @get:Rule
    val activityTestResult = ActivityTestRule<AddModifyCheckListActivity>(AddModifyCheckListActivity::class.java, false, false)

    @Before
    fun setup(){
        declareMock<UserRepository>{
            BDDMockito.given(this.currentUser).willReturn(MutableLiveData(user))
        }

        declareMock<CheckListRepository>{
            BDDMockito.given(this.checkList).willReturn(checkList1)
        }
        activityTestResult.launchActivity(Intent())
    }

    @Test
    @Throws(Exception::class)
    fun recyclerView_isVisible(){
        onView(withId(R.id.add_modify_checklist_rv)).check(matches(isDisplayed()))
    }

    @Test
    @Throws(Exception::class)
    fun checkToolbar_isVisible(){
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
    }

    @Test
    @Throws(Exception::class)
    fun fieldName_isVisible(){
        onView(withId(R.id.add_modify_checklist_list_name)).check(matches(isDisplayed()))
    }

    @Test
    @Throws(Exception::class)
    fun fieldType_isVisible(){
        onView(withId(R.id.add_modify_checklist_list_type)).check(matches(isDisplayed()))
    }


    @Test
    @Throws(Exception::class)
    fun addButton_isVisible(){
        onView(withId(R.id.add_modify_checklist_add_button)).check(matches(isDisplayed()))
    }

    @Test
    @Throws(Exception::class)
    fun deleteButton_isVisible(){
        onView(withId(R.id.add_modify_checklist_delete_button)).check(matches(isDisplayed()))
    }
}