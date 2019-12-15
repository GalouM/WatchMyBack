package com.galou.watchmyback.utils

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.galou.watchmyback.testHelpers.setLocationShadow
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author galou
 * 2019-12-13
 */
@RunWith(AndroidJUnit4::class)
class NetworkUtilTest {

    private val context: Context =  ApplicationProvider.getApplicationContext()

    @Test
    fun whenGPSActive_sendTrue(){
        setLocationShadow(true)
        assertThat(isGPSAvailable(context)).isTrue()
    }

    @Test
    fun whenGPSInactive_sendFalse(){
        setLocationShadow(false)
        assertThat(isGPSAvailable(context)).isFalse()
    }
}