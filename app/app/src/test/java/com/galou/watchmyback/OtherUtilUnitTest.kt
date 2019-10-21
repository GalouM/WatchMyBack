package com.galou.watchmyback

import com.galou.watchmyback.utils.idGenerated
import com.galou.watchmyback.utils.todaysDate
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Test
import java.util.*

/**
 * Created by galou on 2019-10-20
 */
class OtherUtilUnitTest {

    @Test
    fun checkIdGenerated_isUnique(){
        val firstId = idGenerated
        val secondId = idGenerated
        assertThat(firstId, not(secondId))
    }

    @Test
    fun todayDate_isToday(){
        val today = Calendar.getInstance(Locale.CANADA).time.toString()
        assertEquals(today, todaysDate.toString())
    }
}