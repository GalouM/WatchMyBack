package com.galou.watchmyback.utils.extension

import kotlin.math.roundToInt

/**
 * @author galou
 * 2019-12-16
 */

/**
 * Convert a temperature in Kelvin into degree Celsius
 *
 * @return Temperature in degree Celsius
 */
fun Double.kelvinToCelsius(): Int = (this - 273.15).roundToInt()

/**
 * Convert a temperature in Kelvin into degree Fahrenheit
 *
 * @return Temperature in degree Fahrenheit
 */
fun Double.kelvinToFahrenheit(): Int = (this * 1.8 - 459.67).roundToInt()

