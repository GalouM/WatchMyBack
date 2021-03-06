package com.galou.watchmyback.utils.extension

import android.widget.RadioGroup
import androidx.databinding.BindingAdapter
import com.galou.watchmyback.R
import com.galou.watchmyback.addFriend.FetchType
import com.galou.watchmyback.data.entity.TimeDisplay
import com.galou.watchmyback.data.entity.UnitSystem

/**
 * Created by galou on 2019-10-30
 */

/**
 * Determine which radio button of a radio group should be selected depending on the [UnitSystem] selected
 *
 * @param system type of unit system
 *
 * @see BindingAdapter
 */
@BindingAdapter("unitSystemType")
fun RadioGroup.unitSystemType(system: UnitSystem?){
    when(system){
        UnitSystem.METRIC -> this.check(R.id.settings_view_unit_system_unit_metric)
        UnitSystem.IMPERIAL -> this.check(R.id.settings_view_unit_system_unit_imperial)
    }
}

/**
 * Determine which radio button of a radio group should be selected depending on the [TimeDisplay] selected
 *
 * @param timeDisplay way to display time
 *
 * @see BindingAdapter
 */
@BindingAdapter("timeDisplay")
fun RadioGroup.timeDisplay(timeDisplay: TimeDisplay?){
    when(timeDisplay){
        TimeDisplay.H_24 -> this.check(R.id.settings_view_unit_system_time_24)
        TimeDisplay.H_12 -> this.check(R.id.settings_view_unit_system_time_12)
    }
}

/**
 * Determine which radio button of a radio group should be selected depending on the [FetchType] selected
 *
 * @param fetchType type of fetch
 *
 * @see BindingAdapter
 */
@BindingAdapter("searchUserType")
fun RadioGroup.searchUserType(fetchType: FetchType?){
    when(fetchType) {
        FetchType.USERNAME -> check(R.id.add_friend_view_radio_button_username)
        FetchType.EMAIL_ADDRESS -> check(R.id.add_friend_view_radio_button_email)
        null -> return
    }
}

