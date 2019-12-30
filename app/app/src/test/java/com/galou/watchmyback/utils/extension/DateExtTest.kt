package com.galou.watchmyback.utils.extension

import com.galou.watchmyback.data.entity.TimeDisplay
import com.galou.watchmyback.data.entity.UserPreferences
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.util.*

/**
 * @author galou
 * 2019-12-28
 */
class DateExtTest {
    private val calendar = Calendar.getInstance()
    private val userPrefs24 = UserPreferences(timeDisplay = TimeDisplay.H_24)
    private val userPref12 = UserPreferences(timeDisplay = TimeDisplay.H_12)

    @Before
    fun setupDate(){
        calendar.set(Calendar.MINUTE, 30)
        calendar.set(Calendar.HOUR_OF_DAY, 5)
        calendar.set(Calendar.DAY_OF_MONTH, 6)
        calendar.set(Calendar.MONTH, 7)
        calendar.set(Calendar.YEAR, 2012)
    }

    @Test
    fun displayTime24h_returnCorrectString(){
        val date = calendar.time
        assertThat(date.displayTime(userPrefs24)).contains("05:30")
    }

    @Test
    fun displayTime12h_returnCorrectString(){
        val date = calendar.time
        assertThat(date.displayTime(userPref12)).contains("05:30 AM")
    }

    @Test
    fun displayDate24h_returnCorrectString(){
        val date = calendar.time
        assertThat(date.displayDate(userPrefs24)).contains("06/08/2012 - 05:30")
    }

    @Test
    fun displayDate12h_returnCorrectString(){
        val date = calendar.time
        assertThat(date.displayDate(userPref12)).contains("08/06/2012 - 05:30 AM")
    }
}