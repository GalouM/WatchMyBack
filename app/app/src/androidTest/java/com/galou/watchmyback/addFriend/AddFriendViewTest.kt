package com.galou.watchmyback.addFriend

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import androidx.test.rule.ActivityTestRule
import com.galou.watchmyback.R
import org.junit.Rule
import org.junit.Test

/**
 * @author galou
 * 2019-11-16
 */
@MediumTest
class AddFriendViewTest {
    @get:Rule
    val activityTestResult = ActivityTestRule<AddFriendActivity>(AddFriendActivity::class.java)

    @Test
    @Throws(Exception::class)
    fun recyclerView_isVisible(){
        onView(withId(R.id.add_friend_view_rv)).check(matches(isDisplayed()))
    }

    @Test
    @Throws(Exception::class)
    fun checkToolbar_isVisible(){
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))

    }

    @Test
    @Throws(Exception::class)
    fun fieldSearch_isVisible(){
        onView(withId(R.id.add_friend_view_search)).check(matches(isDisplayed()))

    }

    @Test
    @Throws(Exception::class)
    fun radioButton_areVisible(){
        onView(withId(R.id.add_friend_view_radio_button_username)).check(matches(isDisplayed()))
        onView(withId(R.id.add_friend_view_radio_button_email)).check(matches(isDisplayed()))
    }
}