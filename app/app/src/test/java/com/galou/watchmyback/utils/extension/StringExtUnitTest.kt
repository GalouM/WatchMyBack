package com.galou.watchmyback.utils.extension

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Created by galou on 2019-10-25
 */
class StringExtUnitTest {
    @Test
    fun checkNameContainsSpecialCharacter(){
        val myString = "@estt123"
        assertThat(myString.isCorrectName()).isFalse()
    }

    @Test
    fun checkNameTooShort(){
        val myString = "My"
        assertThat(myString.isCorrectName()).isFalse()
    }

    @Test
    fun checkNameNoSpecialCharacter(){
        val myString = "My name"
        assertThat(myString.isCorrectName()).isTrue()
    }

    @Test
    fun checkEmailWithNoAt(){
        val myString = "test"
        assertThat(myString.isCorrectEmail()).isFalse()
    }

    @Test
    fun checkEmailWithNoDomainExt(){
        val myString = "test@test"
        assertThat(myString.isCorrectEmail()).isFalse()
    }

    @Test
    fun checkEmailCorrect(){
        val myString = "test@test.com"
        assertThat(myString.isCorrectEmail()).isTrue()
    }

    @Test
    fun checkPhoneNumberNotOnlyNumbers(){
        val number = "33344ttt"
        assertThat(number.isCorrectPhoneNumber()).isFalse()
    }

    @Test
    fun checkPhoneNumberTooShort(){
        val number = "3334"
        assertThat(number.isCorrectPhoneNumber()).isFalse()
    }

    @Test
    fun checkPhoneNumberTooLong(){
        val number = "33344567861234"
        assertThat(number.isCorrectPhoneNumber()).isFalse()
    }

    @Test
    fun checkPhoneNumberCorrect(){
        val number = "16048030356"
        assertThat(number.isCorrectPhoneNumber()).isTrue()
        val number2 = "+1608030356"
        assertThat(number2.isCorrectPhoneNumber()).isTrue()
    }
}