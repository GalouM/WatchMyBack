package com.galou.watchmyback.utils.extension

import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.databinding.BindingAdapter
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.TimeDisplay
import com.galou.watchmyback.data.entity.UnitSystem
import kotlinx.android.synthetic.main.activity_settings.view.*
import java.lang.IllegalStateException

/**
 * Created by galou on 2019-10-30
 */

@BindingAdapter("unitSystemType")
fun RadioGroup.unitSystemType(system: UnitSystem?){
    when(system){
        UnitSystem.METRIC -> this.check(R.id.settings_view_unit_system_unit_metric)
        UnitSystem.IMPERIAL -> this.check(R.id.settings_view_unit_system_unit_imperial)
    }
}

@BindingAdapter("timeDisplay")
fun RadioGroup.timeDisplay(timeDisplay: TimeDisplay?){
    when(timeDisplay){
        TimeDisplay.H_24 -> this.check(R.id.settings_view_unit_system_time_24)
        TimeDisplay.H_12 -> this.check(R.id.settings_view_unit_system_time_12)
    }
}

fun RadioButton.onClickUnitSystem(): UnitSystem{
    return when(this.id){
        R.id.settings_view_unit_system_unit_metric -> UnitSystem.METRIC
        R.id.settings_view_unit_system_unit_imperial -> UnitSystem.IMPERIAL
        else -> throw IllegalStateException("Unrecognized button")
    }
}

fun RadioButton.onClickTimeDisplay(): TimeDisplay {
    return when(this.id){
        R.id.settings_view_unit_system_time_24 -> TimeDisplay.H_24
        R.id.settings_view_unit_system_time_12 -> TimeDisplay.H_12
        else -> throw IllegalStateException("Unrecognized button")
    }
}
