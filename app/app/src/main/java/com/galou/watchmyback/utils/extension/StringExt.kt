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
        "Rain" -> WeatherCondition.RAIN
        "Thunderstorm" -> WeatherCondition.THUNDERSTORM
        "Snow" -> WeatherCondition.SNOW
        "Mist" -> WeatherCondition.MIST
        "Drizzle" -> WeatherCondition.DRIZZLE
        "Smoke" -> WeatherCondition.SMOKE
        "Haze" -> WeatherCondition.HAZE
        "Dust" -> WeatherCondition.DUST
        "Fog" -> WeatherCondition.FOG
        "Sand" -> WeatherCondition.SAND
        "Ash" -> WeatherCondition.ASH
        "Squall" -> WeatherCondition.SQUALL
        "Tornado" -> WeatherCondition.TORNADO
        "Clear" -> WeatherCondition.CLEAR
        "Clouds" -> WeatherCondition.CLOUDS
        else -> WeatherCondition.UNKNOWN
    }
}