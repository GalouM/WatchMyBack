package com.galou.watchmyback.utilsTest

import android.content.Context
import android.os.Build
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.test.core.app.ApplicationProvider
import com.galou.watchmyback.R
import com.galou.watchmyback.WatchMyBackApplication
import com.galou.watchmyback.data.entity.TimeDisplay
import com.galou.watchmyback.data.entity.UnitSystem
import com.galou.watchmyback.utils.extension.onClickTimeDisplay
import com.galou.watchmyback.utils.extension.onClickUnitSystem
import com.galou.watchmyback.utils.extension.timeDisplay
import com.galou.watchmyback.utils.extension.unitSystemType
import com.google.common.truth.Truth.assertThat
import kotlinx.android.synthetic.main.activity_settings.view.*
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
@RunWith(RobolectricTestRunner::class)
class RadioButtonExtUnitTest : KoinTest{

    private lateinit var context: Context
    private lateinit var metricButton: RadioButton
    private lateinit var imperialButton: RadioButton
    private lateinit var h12Button: RadioButton
    private lateinit var h24Button: RadioButton

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
    }

    @After
    fun close(){
        stopKoin()
    }

    @Test
    fun radioButtonUnitSystem_clickSendRightValue(){
        assertThat(metricButton.onClickUnitSystem()).isEqualTo(UnitSystem.METRIC)

        assertThat(imperialButton.onClickUnitSystem()).isEqualTo(UnitSystem.IMPERIAL)

    }

    @Test
    fun radioButtonTime_clickSendRightValue(){
        assertThat(h12Button.onClickTimeDisplay()).isEqualTo(TimeDisplay.H_12)

        assertThat(h24Button.onClickTimeDisplay()).isEqualTo(TimeDisplay.H_24)

        val group = RadioGroup(context)
    }

    @Test
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
    fun radioButtonGroupTimeDisplay_setCorrectly(){
        val timeDisplayGroup = RadioGroup(context)
        timeDisplayGroup.addView(h12Button)
        timeDisplayGroup.addView(h24Button)

        timeDisplayGroup.timeDisplay(TimeDisplay.H_12)
        assertThat(timeDisplayGroup.checkedRadioButtonId).isEqualTo(h12Button.id)

        timeDisplayGroup.timeDisplay(TimeDisplay.H_24)
        assertThat(timeDisplayGroup.checkedRadioButtonId).isEqualTo(h24Button.id)
    }


}