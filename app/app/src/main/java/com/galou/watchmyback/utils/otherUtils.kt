package com.galou.watchmyback.utils

import java.util.*

/**
 * Created by galou on 2019-10-20
 */

var idGenerated: String = ""
    get() {
        field = UUID.randomUUID().toString()
        return field
    }

var todaysDate: Date = Calendar.getInstance(Locale.CANADA).time