package com.galou.watchmyback.tripMapView

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
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
 * 2019-11-30
 */
@MediumTest
@RunWith(AndroidJUnit4::class)
class TripMapViewTest {

    @get:Rule
    val activityTestResult = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Before
    fun showFriendView(){
        onView(withId(R.id.mapView)).perform(click())
    }

    @Test
    @Throws(Exception::class)
    fun fab_isVisible(){
        onView(withId(R.id.trip_map_view_fab)).check(matches(isDisplayed()))
    }

    @Test
    @Throws(Exception::class)
    fun clickFab_openAddFriendActivity(){
        onView(withId(R.id.trip_map_view_fab)).perform(click())
        onView(withId(R.id.add_trip_view_root)).check(matches(isDisplayed()))
    }
}