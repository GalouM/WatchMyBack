package com.galou.watchmyback.utils.extension

import com.galou.watchmyback.data.entity.WeatherCondition

/**
 * Created by galou on 2019-10-25
 */
/**
 * Check that the string only contain letters and has more than 3 character
 *
 * @return true if the string is conform, false otherwise
 */
fun String.isCorrectName(): Boolean{
    val pattern = "[^a-z ]".toRegex(RegexOption.IGNORE_CASE)
    return !this.contains(pattern) && this.length >= 3

}

/**
 * Check that the string contains a "@" a "." and at least 2 letter after the dot
 *
 * @return true if the string is conform, false otherwise
 */
fun String.isCorrectEmail(): Boolean{
    val emailPart = this.split("@")
    if(emailPart.size > 1){
        val domain = emailPart[1].split(".")
        return domain.size > 1

    }
    return false

}

/**
 * Check if the string contains only numbers or numbers and start with a "+", contains between 10 and 13 characters
 *
 * @return true if the string is conform, false otherwise
 */
fun String.isCorrectPhoneNumber(): Boolean{
    val pattern = "(\\+\\d+)?\\d+".toRegex()
    return this.matches(pattern) &&
            this.length >= 10 &&
            this.length <= 13

}

fun String.toWeatherConditionName(): WeatherCondition {
    return when(this){
        "clear sky" -> WeatherCondition.CLEAR_SKY
        "few clouds" -> WeatherCondition.FEW_CLOUDS
        "scattered clouds" -> WeatherCondition.SCATTERED_CLOUDS
        "broken clouds" -> WeatherCondition.BROKEN_CLOUDS
        "shower rain" -> WeatherCondition.SHOWER_RAIN
        "rain" -> WeatherCondition.RAIN
        "thunderstorm" -> WeatherCondition.THUNDERSTORM
        "snow" -> WeatherCondition.SNOW
        "mist" -> WeatherCondition.MIST
        else -> WeatherCondition.UNKNOWN
    }
}