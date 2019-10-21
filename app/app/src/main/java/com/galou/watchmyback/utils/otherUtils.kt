package com.galou.watchmyback.utils

import java.util.*

/**
 * Created by galou on 2019-10-20
 */

/**
 * Generate a unique and random string using the class UUID
 */
var idGenerated: String = ""
    get() {
        field = UUID.randomUUID().toString()
        return field
    }


/**
 * Generate todays date as Date using the class Calendar
 */
var todaysDate: Date = Calendar.getInstance(Locale.CANADA).time
