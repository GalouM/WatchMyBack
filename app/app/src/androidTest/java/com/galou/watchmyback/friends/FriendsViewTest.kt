package com.galou.watchmyback.friends

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.galou.watchmyback.R
import com.galou.watchmyback.main.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author galou
 * 2019-11-07
 */
@MediumTest
@RunWith(AndroidJUnit4::class)
class FriendsViewTest {

    @get:Rule
    val activityTestResult = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Before
    fun showFriendView(){
        onView(withId(R.id.friendsView)).perform(click())
    }

    @Test
    @Throws(Exception::class)
    fun recyclerView_isVisible(){
        onView(withId(R.id.friends_view_rv)).check(matches(isDisplayed()))
    }

    @Test
    @Throws(Exception::class)
    fun fab_isVisible(){
        onView(withId(R.id.friends_view_fab)).check(matches(isDisplayed()))
    }

    @Test
    @Throws(Exception::class)
    fun clickFab_openAddFriendActivity(){
        onView(withId(R.id.friends_view_fab)).perform(click())
        onView(withId(R.id.add_friends_view_root)).check(matches(isDisplayed()))
    }
}