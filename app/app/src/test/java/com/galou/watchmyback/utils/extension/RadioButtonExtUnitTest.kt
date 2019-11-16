package com.galou.watchmyback.utils.extension

import android.content.Context
import android.os.Build
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.galou.watchmyback.R
import com.galou.watchmyback.WatchMyBackApplication
import com.galou.watchmyback.addFriend.FetchType
import com.galou.watchmyback.data.entity.TimeDisplay
import com.galou.watchmyback.data.entity.UnitSystem
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Created by galou on 2019-10-31
 */
@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(AndroidJUnit4::class)
class RadioButtonExtUnitTest : KoinTest{

    private lateinit var context: Context
    private lateinit var metricButton: RadioButton
    private lateinit var imperialButton: RadioButton
    private lateinit var h12Button: RadioButton
    private lateinit var h24Button: RadioButton
    private lateinit var usernameButton: RadioButton
    private lateinit var emailButton: RadioButton

    @Before
    fun setup(){
        context = ApplicationProvider.getApplicationContext<WatchMyBackApplication>()
        metricButton = RadioButton(context)
        metricButton.id = R.id.settings_view_unit_system_unit_metric
        imperialButton = RadioButton(context)
        imperialButton.id = R.id.settings_view_unit_system_unit_imperial

        h12Button = RadioButton(context)
        h12Button.id = R.id.settings_view_unit_system_time_12
        h24Button = RadioButton(context)
        h24Button.id = R.id.settings_view_unit_system_time_24

        usernameButton = RadioButton(context)
        usernameButton.id = R.id.add_friend_view_radio_button_username
        emailButton = RadioButton(context)
        emailButton.id = R.id.add_friend_view_radio_button_email

    }

    @After
    fun close(){
        stopKoin()
    }


    @Test
    @Throws(Exception::class)
    fun radioButtonGroupUnitSystem_setCorrectly(){
        val unitSystemGroup = RadioGroup(context)
        unitSystemGroup.addView(metricButton)
        unitSystemGroup.addView(imperialButton)

        unitSystemGroup.unitSystemType(UnitSystem.METRIC)
        assertThat(unitSystemGroup.checkedRadioButtonId).isEqualTo(metricButton.id)

        unitSystemGroup.unitSystemType(UnitSystem.IMPERIAL)
        assertThat(unitSystemGroup.checkedRadioButtonId).isEqualTo(imperialButton.id)
    }

    @Test
    @Throws(Exception::class)
    fun radioButtonGroupTimeDisplay_setCorrectly(){
        val timeDisplayGroup = RadioGroup(context)
        timeDisplayGroup.addView(h12Button)
        timeDisplayGroup.addView(h24Button)

        timeDisplayGroup.timeDisplay(TimeDisplay.H_12)
        assertThat(timeDisplayGroup.checkedRadioButtonId).isEqualTo(h12Button.id)

        timeDisplayGroup.timeDisplay(TimeDisplay.H_24)
        assertThat(timeDisplayGroup.checkedRadioButtonId).isEqualTo(h24Button.id)
    }

    @Test
    @Throws(Exception::class)
    fun radioButtonGroupFetchType_setCorrectly(){
        val fetchTypeGroup = RadioGroup(context).apply {
            addView(usernameButton)
            addView(emailButton)
        }

        fetchTypeGroup.searchUserType(FetchType.USERNAME)
        assertThat(fetchTypeGroup.checkedRadioButtonId).isEqualTo(usernameButton.id)

        fetchTypeGroup.searchUserType(FetchType.EMAIL_ADDRESS)
        assertThat(fetchTypeGroup.checkedRadioButtonId).isEqualTo(emailButton.id)
    }


}