package com.galou.watchmyback.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * @author galou
 * 2019-12-05
 */

class ConverterUnitTest {

    @Test
    fun convertDoubleToString_returnCorrectString(){
        val double = 35.5
        assertThat(Converter.doubleToString(double)).contains("35.5")
    }

    @Test
    fun convertNullToString_returnEmptyString(){
        val double = null
        assertThat(Converter.doubleToString(double)).isEmpty()
    }

    @Test
    fun convertStringToDouble_returnCorrectNumber(){
        val string = "35.5"
        assertThat(Converter.stringToDouble(string)).isEqualTo(35.5)
    }

    @Test
    fun convertStringNonNumber_returnNull(){
        val string = "tt"
        assertThat(Converter.stringToDouble(string)).isNull()
    }
}