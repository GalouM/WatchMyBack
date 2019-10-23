package com.galou.watchmyback

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by galou on 2019-10-22
 */

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityTestResult = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Test
    @Throws(Exception::class)
    fun checkBottomNavigation_isVisible(){
        onView(withId(R.id.main_activity_bottom_nav)).check(matches(isDisplayed()))

    }

    @Test
    @Throws(Exception::class)
    fun checkToolbar_isVisible(){
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))

    }

    @Test
    @Throws(Exception::class)
    fun clickBottomNavItem_showCorrectFragment(){
        onView(withId(R.id.mapView)).perform(click())
        onView(withId(R.id.map_view_container)).check(matches(isDisplayed()))

        onView(withId(R.id.tripsView)).perform(click())
        onView(withId(R.id.trip_view_container)).check(matches(isDisplayed()))

        onView(withId(R.id.friendsView)).perform(click())
        onView(withId(R.id.friends_view_container)).check(matches(isDisplayed()))

        onView(withId(R.id.checkListView)).perform(click())
        onView(withId(R.id.check_list_view_container)).check(matches(isDisplayed()))
    }

    @Test
    @Throws(Exception::class)
    fun onClickBurgerMenu_showNavigationDrawer(){
        onView(withContentDescription(R.string.open_nav_drawer)).perform(click())
        onView(withId(R.id.main_activity_navigation_view)).check(matches(isDisplayed()))
    }


}