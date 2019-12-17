package com.galou.watchmyback.utils.extension

/**
 * @author galou
 * 2019-12-16
 */

/**
 * Convert a temperature in Kelvin into degree Celsius
 *
 * @return Temperature in degree Celsius
 */
fun Double.kelvinToCelsius(): Double = this - 273.15

/**
 * Convert a temperature in Kelvin into degree Fahrenheit
 *
 * @return Temperature in degree Fahrenheit
 */
fun Double.kelvinToFahrenheit(): Double = this * 1.8 - 459.67

