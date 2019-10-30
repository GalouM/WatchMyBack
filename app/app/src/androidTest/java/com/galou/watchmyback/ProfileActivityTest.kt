package com.galou.watchmyback

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.profileActivity.ProfileActivity
import com.galou.watchmyback.instrumentedTestHelpers.TEST_UID
import com.galou.watchmyback.instrumentedTestHelpers.generateTestUser
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.given

/**
 * Created by galou on 2019-10-27
 */
@RunWith(AndroidJUnit4::class)
class ProfileActivityTest : KoinTest  {

    private val user = generateTestUser(TEST_UID)

    @get:Rule
    val activityTestResult = ActivityTestRule<ProfileActivity>(ProfileActivity::class.java, false, false)

    @Before
    fun setup(){
        declareMock<UserRepository>{
            given(this.currentUser).willReturn(MutableLiveData(user))
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
    fun checkIfValidateButtonToolbar_isVisible(){
        onView(withId(R.id.validate_menu_validate))
            .check(matches(isDisplayed()))

    }

    @Test
    @Throws(Exception::class)
    fun checkAllFieldInfoUser_areVisible(){
        onView(withId(R.id.profile_view_username))
            .check(matches(isDisplayed()))
        onView(withId(R.id.profile_view_email))
            .check(matches(isDisplayed()))
        onView(withId(R.id.profile_view_phone))
            .check(matches(isDisplayed()))
        onView(withId(R.id.profile_view_picture))
            .check(matches(isDisplayed()))
    }

    @Test
    @Throws(Exception::class)
    fun checkFields_filledUpWithUserInfo(){
        onView(withId(R.id.profile_view_username))
            .check(matches(withText(user.username)))
        onView(withId(R.id.profile_view_email))
            .check(matches(withText(user.email)))
        onView(withId(R.id.profile_view_phone))
            .check(matches(withText(user.phoneNumber)))
    }

    @Test
    @Throws(Exception::class)
    fun checkWrongValue_showError(){
        //replace values
        onView(withId(R.id.profile_view_username))
            .perform(replaceText("@us"))
        onView(withId(R.id.profile_view_phone))
            .perform(replaceText("45675"))
        onView(withId(R.id.profile_view_email))
            .perform(replaceText("wrong@email"))

        //perform click save
        onView(withId(R.id.validate_menu_validate)).perform(click())

        // check error message is displayed
        onView(withText(R.string.incorrect_username))
            .check(matches(isDisplayed()))
        onView(withText(R.string.incorrect_phone_number))
            .check(matches(isDisplayed()))
        onView(withText(R.string.incorrect_email))
            .check(matches(isDisplayed()))
    }



}