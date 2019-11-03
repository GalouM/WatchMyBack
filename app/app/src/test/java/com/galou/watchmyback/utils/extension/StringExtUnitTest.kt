package com.galou.watchmyback.utils.extension

import com.galou.watchmyback.utils.extension.isCorrectEmail
import com.galou.watchmyback.utils.extension.isCorrectName
import com.galou.watchmyback.utils.extension.isCorrectPhoneNumber
import junit.framework.Assert
import org.junit.Test

/**
 * Created by galou on 2019-10-25
 */
class StringExtUnitTest {
    @Test
    fun checkNameContainsSpecialCharacter(){
        val myString = "@estt123"
        Assert.assertFalse(myString.isCorrectName())
    }

    @Test
    fun checkNameTooShort(){
        val myString = "My"
        Assert.assertFalse(myString.isCorrectName())
    }

    @Test
    fun checkNameNoSpecialCharacter(){
        val myString = "My name"
        Assert.assertTrue(myString.isCorrectName())
    }

    @Test
    fun checkEmailWithNoAt(){
        val myString = "test"
        Assert.assertFalse(myString.isCorrectEmail())
    }

    @Test
    fun checkEmailWithNoDomainExt(){
        val myString = "test@test"
        Assert.assertFalse(myString.isCorrectEmail())
    }

    @Test
    fun checkEmailCorrect(){
        val myString = "test@test.com"
        Assert.assertTrue(myString.isCorrectEmail())
    }

    @Test
    fun checkPhoneNumberNotOnlyNumbers(){
        val number = "33344ttt"
        Assert.assertFalse(number.isCorrectPhoneNumber())
    }

    @Test
    fun checkPhoneNumberTooShort(){
        val number = "3334"
        Assert.assertFalse(number.isCorrectPhoneNumber())
    }

    @Test
    fun checkPhoneNumberTooLong(){
        val number = "33344567861234"
        Assert.assertFalse(number.isCorrectPhoneNumber())
    }

    @Test
    fun checkPhoneNumberCorrect(){
        val number = "16048030356"
        Assert.assertTrue(number.isCorrectPhoneNumber())
        val number2 = "+1608030356"
        Assert.assertTrue(number2.isCorrectPhoneNumber())
    }
}