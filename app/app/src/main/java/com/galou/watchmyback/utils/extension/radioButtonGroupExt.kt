package com.galou.watchmyback.utils.extension

import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.databinding.BindingAdapter
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.TimeDisplay
import com.galou.watchmyback.data.entity.UnitSystem

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

