package com.galou.watchmyback.utils.extension

import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.TimeDisplay
import com.galou.watchmyback.data.entity.UnitSystem

/**
 * @author galou
 * 2019-11-07
 */

/**
 * Get ID of a button and return the Unit system corresponding
 *
 * @return [UnitSystem] corresponding to the button
 *
 * @see UnitSystem
 */
fun Int.onClickUnitSystem(): UnitSystem {
    return when(this){
        R.id.settings_view_unit_system_unit_metric -> UnitSystem.METRIC
        R.id.settings_view_unit_system_unit_imperial -> UnitSystem.IMPERIAL
        else -> throw IllegalStateException("Unrecognized button")
    }
}

/**
 * Get ID of a button and return the Time Display corresponding
 *
 * @return [TimeDisplay] corresponding to the button
 *
 * @see TimeDisplay
 */
fun Int.onClickTimeDisplay(): TimeDisplay {
    return when(this){
        R.id.settings_view_unit_system_time_24 -> TimeDisplay.H_24
        R.id.settings_view_unit_system_time_12 -> TimeDisplay.H_12
        else -> throw IllegalStateException("Unrecognized button")
    }
}