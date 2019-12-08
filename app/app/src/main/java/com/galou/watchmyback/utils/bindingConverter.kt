package com.galou.watchmyback.utils

import androidx.databinding.InverseMethod

/**
 * @author galou
 * 2019-12-05
 */

/**
 * Converter used to create complexe two way data binding
 */
object Converter {

    /**
     * Convert a Double into string if not null
     *
     * @param value double to convert
     * @return the number converted into a string or an empty string if the number is null
     *
     * @see InverseMethod
     */
    @JvmStatic
    @InverseMethod("stringToDouble")
    fun doubleToString(value: Double?): String? {
        return value?.toString() ?: ""

    }

    /**
     * Convert a String into a double
     *
     * @param value string to convert
     * @return  Double or null if the value can't be converted into a double
     */
    @JvmStatic
    fun stringToDouble(value: String?): Double? {
        return value?.toDoubleOrNull()
    }
}