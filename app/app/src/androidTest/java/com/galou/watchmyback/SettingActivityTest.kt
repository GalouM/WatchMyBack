package com.galou.watchmyback

import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.galou.watchmyback.data.entity.TimeDisplay
import com.galou.watchmyback.data.entity.UnitSystem
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.instrumentedTestHelpers.*
import com.galou.watchmyback.settings.SettingsActivity
import com.galou.watchmyback.settings.SettingsViewModel
import com.galou.watchmyback.utils.Result
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.viewmodel.ext.android.viewModel
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
    var instantExecutorRule = InstantTaskExecutorRule()


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

        when(preferencesTest.unitSystem){
            UnitSystem.METRIC -> {
                onView(withId(R.id.settings_view_unit_system_unit_metric))
                    .check(matches(isChecked()))
            }
            UnitSystem.IMPERIAL -> {
                onView(withId(R.id.settings_view_unit_system_unit_imperial))
                    .check(matches(isChecked()))
            }
        }

        when(preferencesTest.timeDisplay){
            TimeDisplay.H_24 -> {
                onView(withId(R.id.settings_view_unit_system_time_24))
                    .check(matches(isChecked()))
            }
            TimeDisplay.H_12 -> {
                onView(withId(R.id.settings_view_unit_system_time_12))
                    .check(matches(isChecked()))
            }
        }

    }

    @Test
    @Throws(Exception::class)
    fun onClickNotifications_update_preferences(){
        val preferences = activityTestResult.activity.viewModel<SettingsViewModel>().value.preferencesLD

        if(preferences.value?.notificationEmergency == false){
            onView(withId(R.id.settings_view_notification_emergency_switch))
                .perform(click())
        }
        assertEquals(preferences.value?.notificationEmergency, true)

        if(preferences.value?.notificationBackSafe == true){
            onView(withId(R.id.settings_view_notification_back_switch))
                .perform(click())
        }
        assertEquals(preferences.value?.notificationBackSafe, false)

        if(preferences.value?.notificationLate == true){
            onView(withId(R.id.settings_view_notification_late_switch))
                .perform(click())
        }
        assertEquals(preferences.value?.notificationLate, false)

        if(preferences.value?.notificationLocationUpdate == true){
            onView(withId(R.id.settings_view_notification_update_switch))
                .perform(click())
        }
        assertEquals(preferences.value?.notificationLocationUpdate, false)

    }


    @Test
    @Throws(Exception::class)
    fun clickMetricData_updatePreferences(){
        val preferences = activityTestResult.activity.viewModel<SettingsViewModel>().value.preferencesLD

        if(preferences.value?.unitSystem == UnitSystem.METRIC){
            onView(withId(R.id.settings_view_unit_system_unit_imperial))
                .perform(click())
        }
        assertEquals(preferences.value?.unitSystem, UnitSystem.IMPERIAL)

        if(preferences.value?.timeDisplay == TimeDisplay.H_24){
            onView(withId(R.id.settings_view_unit_system_time_12))
                .perform(click())
        }
        assertEquals(preferences.value?.timeDisplay, TimeDisplay.H_12)
    }

    @Test
    @Throws(Exception::class)
    fun changeEmergencyNumber_updatePreferences() {
        val preferences = activityTestResult.activity.viewModel<SettingsViewModel>().value.preferencesLD

        val newNumber = "112"
        onView(withId(R.id.settings_view_emergency_number))
            .perform(replaceText(newNumber))
        assertEquals(preferences.value?.emergencyNumber, newNumber)
    }



}