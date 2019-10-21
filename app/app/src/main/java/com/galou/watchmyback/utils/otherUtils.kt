package com.galou.watchmyback.utils

import java.util.*

/**
 * Utils class use to generate data
 *
 * @author Galou Minisini
 */

/**
 * Generate a unique and random [String] using the class [UUID]
 */
var idGenerated: String = ""
    get() {
        field = UUID.randomUUID().toString()
        return field
    }


/**
 * Generate todays date as [Date] using the class [Calendar]
 */
var todaysDate: Date = Calendar.getInstance(Locale.CANADA).time
