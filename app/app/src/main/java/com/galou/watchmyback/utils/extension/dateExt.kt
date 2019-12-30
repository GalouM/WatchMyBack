package com.galou.watchmyback.utils.extension

import com.galou.watchmyback.data.entity.UserPreferences
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author galou
 * 2019-12-27
 */

infix fun Date.displayTime(userPrefs: UserPreferences): String {
    val formatter = SimpleDateFormat(userPrefs.timeDisplay.displayTimePattern, Locale.getDefault())
    return formatter.format(this)

}

infix fun Date.displayDate(userPrefs: UserPreferences): String {
    val formatter = SimpleDateFormat(userPrefs.timeDisplay.displayDatePattern, Locale.getDefault())
    return formatter.format(this)
}