package com.galou.watchmyback.largeTest

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.instrumentedTestHelpers.TEST_UID
import com.galou.watchmyback.instrumentedTestHelpers.generateTestUser
import com.galou.watchmyback.main.MainActivity
import org.junit.Before
import org.junit.Rule
import org.koin.test.KoinTest
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito

/**
 * @author galou
 * 2019-11-03
 */
@LargeTest
class UserActionTest : KoinTest {

    private val user = generateTestUser(TEST_UID)

    @get:Rule
    val activityTestResult = ActivityTestRule<MainActivity>(MainActivity::class.java, false, false)

    @Before
    fun setup(){
        declareMock<UserRepository>{
            BDDMockito.given(this.currentUser).willReturn(MutableLiveData(user))
        }
        activityTestResult.launchActivity(Intent())
    }

}