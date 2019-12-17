package com.galou.watchmyback.utils.extension

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * @author galou
 * 2019-12-16
 */
class DoubleExtTest {

    @Test
    @Throws(Exception::class)
    fun kelvinToCelsius(){
        val temperatureInKelvin = 500.0
        assertThat(temperatureInKelvin.kelvinToCelsius()).isEqualTo(temperatureInKelvin - 273.15)
    }

    @Test
    @Throws(Exception::class)
    fun kelvinToFahrenheit(){
        val temperatureInKelvin = 500.0
        assertThat(temperatureInKelvin.kelvinToFahrenheit()).isEqualTo(temperatureInKelvin * 1.8 - 459.67)
    }
}