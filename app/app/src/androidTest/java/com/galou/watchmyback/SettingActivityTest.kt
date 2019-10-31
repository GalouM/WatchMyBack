package com.galou.watchmyback

import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.instrumentedTestHelpers.*
import com.galou.watchmyback.settings.SettingsActivity
import com.galou.watchmyback.settings.SettingsViewModel
import com.galou.watchmyback.utils.Result
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito
import org.mockito.BDDMockito.given

/**
 * Created by galou on 2019-10-31
 */
@RunWith(AndroidJUnit4::class)
class SettingActivityTest : KoinTest {

    private val user = generateTestUser(TEST_UID)


    @get:Rule
    val activityTestResult = ActivityTestRule<SettingsActivity>(SettingsActivity::class.java, false, false)

    @Before
    fun setup(){
        declareMock<UserRepository>{
            given(this.currentUser).willReturn(MutableLiveData(user))
            given(this.userPreferences).willReturn(MutableLiveData(preferencesTest))
        }
        activityTestResult.launchActivity(Intent())
    }

    @Test
    @Throws(Exception::class)
    fun checkToolbar_isVisible(){
        onView(withId(R.id.toolbar))
            .check(matches(isDisplayed()))
    }

    @Test
    @Throws(Exception::class)
    fun checkAllFieldsAreVisible(){
        onView(withId(R.id.settings_view_notification_back_switch))
            .perform(closeSoftKeyboard(), scrollTo())
            .check(matches(isDisplayed()))
        onView(withId(R.id.settings_view_notification_emergency_switch))
            .perform(closeSoftKeyboard(), scrollTo())
            .check(matches(isDisplayed()))
        onView(withId(R.id.settings_view_notification_update_switch))
            .perform(closeSoftKeyboard(), scrollTo())
            .check(matches(isDisplayed()))
        onView(withId(R.id.settings_view_notification_late_switch))
            .perform(closeSoftKeyboard(), scrollTo())
            .check(matches(isDisplayed()))
        onView(withId(R.id.settings_view_emergency_number))
            .perform(closeSoftKeyboard(), scrollTo())
            .check(matches(isDisplayed()))
        onView(withId(R.id.settings_view_unit_system_time_12))
            .perform(closeSoftKeyboard(), scrollTo())
            .check(matches(isDisplayed()))
        onView(withId(R.id.settings_view_unit_system_time_24))
            .perform(closeSoftKeyboard(), scrollTo())
            .check(matches(isDisplayed()))
        onView(withId(R.id.settings_view_unit_system_unit_metric))
            .perform(closeSoftKeyboard(), scrollTo())
            .check(matches(isDisplayed()))
        onView(withId(R.id.settings_view_unit_system_unit_imperial))
            .perform(closeSoftKeyboard(), scrollTo())
            .check(matches(isDisplayed()))
        onView(withId(R.id.settings_view_data_delete_button))
            .perform(closeSoftKeyboard(), scrollTo())
            .check(matches(isDisplayed()))
    }

    @Test
    @Throws(Exception::class)
    fun checkFields_fillUpWithUserInfo(){
        onView(withId(R.id.settings_view_emergency_number))
            .check(matches(withText(preferencesTest.emergencyNumber)))

        onView(withId(R.id.settings_view_notification_late_switch))
            .check(matches(hasCorrectValue(TEST_NOTIFICATION_LATE)))
        onView(withId(R.id.settings_view_notification_update_switch))
            .check(matches(hasCorrectValue(TEST_NOTIFICATION_UPDATE)))
        onView(withId(R.id.settings_view_notification_emergency_switch))
            .check(matches(hasCorrectValue(TEST_NOTIFICATION_EMERGENCY)))
        onView(withId(R.id.settings_view_notification_back_switch))
            .check(matches(hasCorrectValue(TEST_NOTIFICATION_BACK)))

    }
}